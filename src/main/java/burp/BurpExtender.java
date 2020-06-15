/*
 * Note - you need to rename this file to BurpExtender.java before compiling it
 */

package burp;

import burp.BurpMacroStartAction;
import burp.IBurpExtender;
import burp.IBurpExtenderCallbacks;
import burp.IContextMenuFactory;
import burp.IContextMenuInvocation;
import burp.IHttpListener;
import burp.IHttpRequestResponse;
import burp.IHttpService;
import burp.IInterceptedProxyMessage;
import burp.IProxyListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.AbstractButton;
import javax.swing.JCheckBoxMenuItem;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import org.zaproxy.zap.extension.automacrobuilder.PRequestResponse;
import org.zaproxy.zap.extension.automacrobuilder.ParmGen;
import org.zaproxy.zap.extension.automacrobuilder.ParmGenMacroTrace;
import org.zaproxy.zap.extension.automacrobuilder.ParmGenUtil;
import org.zaproxy.zap.extension.automacrobuilder.ParmVars;
import org.zaproxy.zap.extension.automacrobuilder.Encode;
import org.zaproxy.zap.extension.automacrobuilder.InterfaceLangOKNG;
import org.zaproxy.zap.extension.automacrobuilder.LockInstance;
import org.zaproxy.zap.extension.automacrobuilder.generated.LangSelectDialog;
import org.zaproxy.zap.extension.automacrobuilder.PRequest;
import org.zaproxy.zap.extension.automacrobuilder.PResponse;
import org.zaproxy.zap.extension.automacrobuilder.ParmGenJSONSave;
import org.zaproxy.zap.extension.automacrobuilder.ParmGenMacroTraceProvider;
import org.zaproxy.zap.extension.automacrobuilder.ThreadManagerProvider;
import org.zaproxy.zap.extension.automacrobuilder.generated.ParmGenTop;



public class BurpExtender implements IBurpExtender,IHttpListener
{
    public static IBurpExtenderCallbacks mCallbacks;
    BurpExtenderDoActionProvider provider = null;
    BurpHelpers helpers;
    MacroBuilder mbr = null;
    IHttpRequestResponse[] selected_messageInfo = null;
    JMenuItem repeatermodeitem = null;
    private static org.apache.logging.log4j.Logger LOGGER4J =
            org.apache.logging.log4j.LogManager.getLogger();

    private void ProcessHTMLComments(String message, String host, String url)
    {
        try
        {
            // Create matcher
            Pattern pattern = ParmGenUtil.Pattern_compile("<!--\n{0,}.+?\n{0,}-->");
            Matcher matcher = pattern.matcher(message);
            boolean printed = false;

            // Find all matches and print the url only one time
            while (matcher.find())
            {
                if (!printed)
                {
                    String header = "HTML COMMENT IN:" + host + url + "\r\n==========================";
                    ParmVars.plog.debuglog(1,header);
                    SaveToFile(host, header, true);
                    printed = true;
                }
                // Get the matching string
                String comment = matcher.group();
                ParmVars.plog.debuglog(1,comment);
                SaveToFile(host, comment, false);
            }
        }
        catch (Exception e)
        {
            ParmVars.plog.printException(e);
        }
    }

    private void SaveToFile(String fileName, String st2write, boolean printTime)
    {
        File aFile = new File(fileName + ".txt");
        Date now = new Date();
        try
        {
            BufferedWriter out = new BufferedWriter(new FileWriter(aFile, aFile.exists()));
            if (printTime)
            {
                out.write("\r\n\r\n" + now.toString() + "\r\n");
            }
            out.write(st2write + "\r\n");
            out.close();
        }
        catch (IOException e)
        {
            ParmVars.plog.printException(e);
        }
    }
    
    public static String getToolname(int toolflag){
        String toolname = "";
                switch(toolflag){
                    case IBurpExtenderCallbacks.TOOL_INTRUDER:
                        toolname ="INTRUDER";
                        break;
                    case IBurpExtenderCallbacks.TOOL_REPEATER:
                        toolname = "REPEATER";
                         break;
                    case IBurpExtenderCallbacks.TOOL_COMPARER:
                        toolname = "COMPARER";
                        break;
                    case IBurpExtenderCallbacks.TOOL_DECODER:
                        toolname = "DECODER";
                         break;
                    case IBurpExtenderCallbacks.TOOL_EXTENDER:
                        toolname ="EXTENDER";
                        break;
                    case IBurpExtenderCallbacks.TOOL_PROXY:
                        toolname ="PROXY";
                        break;
                    case IBurpExtenderCallbacks.TOOL_SEQUENCER:
                        toolname ="SEQUENCER";
                        break;
                    case IBurpExtenderCallbacks.TOOL_SPIDER:
                        toolname ="SPIDER";
                        break;
                    case IBurpExtenderCallbacks.TOOL_SUITE:
                        toolname ="SUITE";
                        break;
                    case IBurpExtenderCallbacks.TOOL_TARGET:
                        toolname ="TARGET";
                        break;
                    case IBurpExtenderCallbacks.TOOL_SCANNER:
                        toolname ="SCANNER";
                        break;
                    default:
                        toolname ="UNKNOWN TOOL.";
                        break;
                }
                return toolname;
    }
    
    private Encode analyzeCharset(IHttpRequestResponse[] messageInfo){
        
        List<PResponse> resopt = null;
        
        if(messageInfo!=null&&messageInfo.length>0){
            resopt = Arrays.stream(messageInfo).map(minfo -> new PResponse(minfo.getResponse(), Encode.ISO_8859_1)).collect(Collectors.toList());
        }
        
        return Encode.analyzeCharset(resopt);
    }
    
    private ArrayList <PRequestResponse> convertMessageInfoToArray(IHttpRequestResponse[] messageInfo, int toolflg){
        ArrayList <PRequestResponse> messages = new ArrayList<PRequestResponse>() ;
        try {
            
            
            for(int i = 0; i< messageInfo.length; i++){
                byte[] binreq = new String("").getBytes(Encode.ISO_8859_1.getIANACharset());//length 0 String byte
                byte[] binres = new String("").getBytes(Encode.ISO_8859_1.getIANACharset());//length 0 String byte
                String res = "";
                IHttpService iserv = null;
                if (messageInfo[i].getRequest() != null){
                    binreq = messageInfo[i].getRequest();
                    iserv = messageInfo[i].getHttpService();
                }
                if(messageInfo[i].getResponse()!=null){
                    binres = messageInfo[i].getResponse();
                }
                if(iserv != null){
                    boolean ssl = (iserv.getProtocol().toLowerCase().equals("https")?true:false);
                    switch(toolflg){
                        case IBurpExtenderCallbacks.TOOL_INTRUDER:
                            //remove special § chars
                            PRequest cleanreq = new PRequest(iserv.getHost(), iserv.getPort(), ssl, binreq, Encode.ISO_8859_1).newRequestWithRemoveSpecialChars(null);
                            binreq = cleanreq.getByteMessage();
                            break;
                        default:
                            break;
                    }
                    
                    messages.add(new PRequestResponse(iserv.getHost(), iserv.getPort(), ssl, binreq, binres, ParmVars.enc));
                }else{
                    messages.add(new PRequestResponse("", 0, false, binreq, binres, ParmVars.enc));
                }
            }
        }catch(Exception e){
            ParmVars.plog.printException(e);
            return null;
        }
        return messages;
    }

   private PRequestResponse convertMessageInfoToPRR(IHttpRequestResponse messageInfo){
       PRequestResponse prr = null;
        try {

                byte[] binreq = new String("").getBytes(Encode.ISO_8859_1.getIANACharset());//length 0 String byte
                byte[] binres = new String("").getBytes(Encode.ISO_8859_1.getIANACharset());//length 0 String byte
                IHttpService iserv = null;
                if (messageInfo.getRequest() != null){
                    binreq = messageInfo.getRequest();
                    iserv = messageInfo.getHttpService();
                }
                if(messageInfo.getResponse()!=null){
                    binres = messageInfo.getResponse();
                }
                if(iserv !=null){
                    boolean ssl = (iserv.getProtocol().toLowerCase().equals("https")?true:false);
                    prr = new PRequestResponse(iserv.getHost(), iserv.getPort(), ssl, binreq, binres, ParmVars.enc);
                }else{
                    prr = new PRequestResponse("", 0, false, binreq, binres, ParmVars.enc);
                }

        }catch(Exception e){
            LOGGER4J.error("", e);
            return null;
        }
        return prr;
    }

    private BurpExtenderDoActionProvider getProvider(int toolflag, boolean messageIsRequest, IHttpRequestResponse messageInfo){
        if ( this.provider == null ) {
            this.provider = new BurpExtenderDoActionProvider();
        }
        this.provider.setParameters(toolflag, messageIsRequest, messageInfo);
        return this.provider;
    }
    
    @Override
    public void processHttpMessage(
        	int toolflag,
            boolean messageIsRequest,
            IHttpRequestResponse messageInfo ){
        ThreadManagerProvider.getThreadManager().beginProcess(getProvider(toolflag, messageIsRequest, messageInfo));
    }




    class NewMenu implements IContextMenuFactory, InterfaceLangOKNG
    {

        IHttpRequestResponse[] messageInfo = null;
        IHttpRequestResponse[] repeaterbaseline = null;
        int toolflg = -1;

        @Override
        public List<JMenuItem> createMenuItems(IContextMenuInvocation icmi) {

            ArrayList<JMenuItem> items = new ArrayList<JMenuItem>();
            
            toolflg = icmi.getToolFlag();
            messageInfo = icmi.getSelectedMessages();
            
            JMenuItem item = new JMenuItem("■Custom■");
            JMenuItem itemmacro = new JMenuItem("■SendTo MacroBuilder■");
            
            if(ParmGenMacroTraceProvider.getOriginalBase().isBaseLineMode()){
                boolean hasMenu = false;
                String menutitle = "■Update Baseline■";
                String tooltip = "Update Baseline: You can tamper tracking tokens which is such like CSRF tokens.";
                switch(toolflg){
                    case IBurpExtenderCallbacks.TOOL_REPEATER:
                        
                        repeaterbaseline = messageInfo;
                        hasMenu = true;
                        break;
                    case IBurpExtenderCallbacks.TOOL_SCANNER:
                    case IBurpExtenderCallbacks.TOOL_INTRUDER:
                        menutitle = "■Clear Baseline■";
                        tooltip = "Clear Baseline: You should select this menu when  you used repeater  in baseline mode.";
                        if(ParmGenMacroTraceProvider.getOriginalBase().getToolBaseline()!=null){
                            hasMenu = true;
                        }
                    default:
                        repeaterbaseline = null;
                        break;
                }
                if(hasMenu){
                    repeatermodeitem = new JMenuItem(menutitle);
                    repeatermodeitem.setToolTipText(tooltip);

                    repeatermodeitem.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {

                            String toolname = getToolname(toolflg);
                            LOGGER4J.debug("updatebaselineAction:" + toolname + ":" + (repeaterbaseline==null?"NULL":"NONULL"));
                            UpdateToolBaseline(repeaterbaseline);
                            }
                    });
                }else{
                    repeatermodeitem = null;
                }
            
            }else{
                repeatermodeitem = null;
            }
            
            

            item.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    menuItemClicked(messageInfo, toolflg);
                }
            });
            itemmacro.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    menuAddRequestsClicked(messageInfo);
                }
            });
            
            
            items.add(itemmacro);
            items.add(item);
            if(repeatermodeitem!=null){
                items.add(repeatermodeitem);
            }
            
            

            return items;
        }

        public void menuItemClicked( IHttpRequestResponse[] messageInfo, int toolflg)
        {
            try
            {
                //選択したリクエストレスポンス
                //プロキシヒストリのリクエストレスポンス
                //IHttpRequestResponse[] allmessages = mCallbacks.getProxyHistory();
                ParmGen pgen = new ParmGen(ParmGenMacroTraceProvider.getOriginalBase());
                if(pgen.twin==null){
                    pgen.twin = new ParmGenTop(ParmGenMacroTraceProvider.getOriginalBase(), new ParmGenJSONSave(ParmGenMacroTraceProvider.getOriginalBase(),
                        convertMessageInfoToArray(messageInfo, toolflg))
                        );
                }
                pgen.twin.VisibleWhenJSONSaved(mbr.getUiComponent());
            }
            catch (Exception e)
            {
                LOGGER4J.error("", e);
            }
        }

        
        public void menuAddRequestsClicked( IHttpRequestResponse[] messageInfo)
        {
            if(ParmGenMacroTraceProvider.getOriginalBase()!=null){
                if(ParmGenMacroTraceProvider.getOriginalBase().getRlistCount()<=0){
                    Encode lang = analyzeCharset(messageInfo);
                    new LangSelectDialog(null, this, lang, false).setVisible(true);
                }else{
                    LangOK();
                }
            }
        }
        
        public void UpdateToolBaseline( IHttpRequestResponse[] messageInfo){
            if(ParmGenMacroTraceProvider.getOriginalBase()!=null){
                if(messageInfo!=null&& messageInfo.length>0){
                    IHttpRequestResponse minfo = messageInfo[0];

                    ParmGenMacroTraceProvider.getOriginalBase().setToolBaseLine(convertMessageInfoToPRR(minfo));
                }else{
                    ParmGenMacroTraceProvider.getOriginalBase().setToolBaseLine(null);
                }
            }
                
        }
                
        @Override
        public void LangOK() {
            if(messageInfo!=null){
                if(mbr!=null){
                //選択したリクエストレスポンス
                    mbr.addNewRequests(
                        convertMessageInfoToArray(messageInfo, toolflg));
                }
            }
        }

        @Override
        public void LangCANCEL() {
            /*** NOP ****/
        }
    }


    //カスタム機能の登録
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks)
    {
        helpers = new BurpHelpers(callbacks.getHelpers());
        //burp 標準出力、標準エラー
        // obtain our output and error streams
        PrintWriter stdout = new PrintWriter(callbacks.getStdout(), true);
        PrintWriter stderr = new PrintWriter(callbacks.getStderr(), true);
        //ParmVars.plog.SetBurpPrintStreams(stdout, stderr);
        //LockInstance locker = new LockInstance();
        
        //セッション管理
        callbacks.registerSessionHandlingAction(new BurpMacroStartAction());
        //callbacks.registerSessionHandlingAction(new BurpMacroLogAction());
    	//コンテキストメニューの追加：　マウス右クリックポップアップメニュー->[my menu item]
        callbacks.registerContextMenuFactory(new NewMenu());
        // register ourselves as an HTTP listener
        callbacks.registerHttpListener(this);
        // register proxy lister
        //callbacks.registerProxyListener(this);
        //MacroBuilderタブ
        mbr = new MacroBuilder(ParmGenMacroTraceProvider.getOriginalBase());
        callbacks.addSuiteTab(mbr);
        mCallbacks = callbacks;
    }

}
