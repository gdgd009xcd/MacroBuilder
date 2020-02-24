/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burp;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import javax.json.stream.JsonParser;

//import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.util.Base64;
import java.util.List;
import org.apache.log4j.Logger;



/**
 *
 * @author daike
 */
public class ParmGenJSON {
    private static org.apache.log4j.Logger logger = Logger.getLogger(ParmGenJSON.class);
    //--loaded values
    private String Version;
    private Encode enc;
    private List<String> ExcludeMimeTypes = null;
    private ArrayList<AppParmsIni> rlist;
    private ArrayList<PRequestResponse> ReqResList;
    private int currentrequest;
    private boolean ProxyInScope;
    private boolean IntruderInScope;
    private boolean RepeaterInScope;
    private boolean ScannerInScope;
    //---------------
    
    
    private AppParmsIni aparms;
    private AppValue apv;
    private List<String> JSONSyntaxErrors;
    private List<Exception> ExceptionErrors;
    private int row = 0;
    
    //PRequestResponse params
    private String PRequest64;
    private String PResponse64;
    private String Host;
    private int Port;
    private boolean SSL;
    private String Comments;
    private boolean Disabled;
    private boolean Error;





    ParmGenJSON(){
        ProxyInScope = false;
        IntruderInScope = false;
        RepeaterInScope = false;
        ScannerInScope = false;
        Version = "";
        enc = Encode.UTF_8;
        ExcludeMimeTypes = new ArrayList<>();
    	rlist = new ArrayList<AppParmsIni>();
        aparms = null;
        apv = null;
        ReqResList = new ArrayList<PRequestResponse>();
        currentrequest = 0;
        row = 0;
        JSONSyntaxErrors = new ArrayList<>();
        ExceptionErrors = new ArrayList<>();
        initReqRes();

    }
    
    public String getVersion(){
        return Version;
    }
    
    public Encode getEncode(){
        return enc;
    }
    
    
    
    private boolean hasErrors(){
        if(JSONSyntaxErrors.size()>0||ExceptionErrors.size()>0){
            return true;
        }
        return false;
    }

    private void initReqRes(){
        PRequest64 = null;
        PResponse64 = null;
        Host = null;
        Port = 0;
        SSL = false;
        Comments = "";
        Disabled = false;
        Error = false;
    }

    ArrayList<AppParmsIni> Getrlist(){
        return rlist;
    }

    ArrayList<PRequestResponse> GetMacroRequests(){
        return ReqResList;
    }
    int getCurrentRequest(){
        return currentrequest;
    }

    private String GetString(JsonParser.Event ev, Object value, String defval){
        String v = "";
        switch(ev){
            case VALUE_STRING:
                try{
                    v =  (String)value;
                }catch(Exception e){
                    v ="";
                }
                break;
            default:
                v = defval;
                break;

        }
        return v;
    }

    private int GetNumber(JsonParser.Event ev, Object value, int defval){
        int i = 0;
        switch(ev){
            case VALUE_NUMBER:
                try{
                    String vstring = (String)value;
                    i =  (int)Integer.parseInt(vstring);
                }catch(Exception e){
                    i = 0;
                }
                break;
            default:
                i = defval;
                break;

        }
        return i;
    }

    private boolean Getboolean(JsonParser.Event ev, Object value, boolean defval){
        boolean b = false;
        switch(ev){
            case VALUE_FALSE:
                b = false;
                break;
            case VALUE_TRUE:
                b = true;
                break;
            default:
                b = defval;
                break;
        }

        return b;
    }

    boolean Parse(ParmGenStack<String> astack, int alevel, JsonParser.Event ev, String name, Object value ){
        String current = astack.getCurrent();
        switch(alevel){
            case 0:
                switch(ev){
                    case END_ARRAY:
                        
                        break;
                    default:
                        if(name.toUpperCase().equals("LANG")){
                            enc = Encode.getEnum(GetString(ev, value, "UTF-8"));
                        }else if(name.toUpperCase().equals("PROXYINSCOPE")){
                            ProxyInScope = Getboolean(ev, value, false);
                        }else if(name.toUpperCase().equals("INTRUDERINSCOPE")){
                            IntruderInScope = Getboolean(ev,value, true);
                        }else if(name.toUpperCase().equals("REPEATERINSCOPE")){
                            RepeaterInScope = Getboolean(ev, value, true);
                        }else if(name.toUpperCase().equals("SCANNERINSCOPE")){
                            ScannerInScope = Getboolean(ev, value, true);
                        }else if(name.toUpperCase().equals("CURRENTREQUEST")){
                            currentrequest = GetNumber(ev, value,0);
                        }else if(name.toUpperCase().equals("VERSION")){
                            Version = GetString(ev, value, "");
                        }
                        break;
                }
                break;
            case 1:
                switch(ev){
                    case START_OBJECT:
                        if(current!=null&&current.toUpperCase().equals("APPPARMSINI_LIST")){
                            //ParmVars.plog.debuglog(0, "START_OBJECT level1 name:" + current);
                            aparms = new AppParmsIni();//add new record
                            aparms.parmlist = new ArrayList<AppValue>();
                        }else if(current!=null&&current.toUpperCase().equals("PREQUESTRESPONSE")){
                            initReqRes();
                        }
                        break;
                    case END_OBJECT:
                        if(!hasErrors()){
                            if(current!=null&&current.toUpperCase().equals("APPPARMSINI_LIST")){
                                if(aparms!=null&&rlist!=null){
                                    if(aparms.getType()==AppParmsIni.T_CSV){
                                        String decodedname = "";
                                        try{
                                                decodedname = URLDecoder.decode(aparms.csvname, "UTF-8");
                                                aparms.frl = new FileReadLine(decodedname, true);
                                        }catch(Exception e){
                                            logger.error("decode failed:[" + aparms.csvname + "]", e);
                                            ExceptionErrors.add(e);
                                        }
                                    }

                                    //aparms.setRow(row);row++;
                                    //aparms.crtGenFormat(true);
                                    rlist.add(aparms);
                                }
                                aparms = null;
                            }else if(current!=null&&current.toUpperCase().equals("PREQUESTRESPONSE")){
                                if(PRequest64!=null){
                                    byte[] binreq = Base64.getDecoder().decode(PRequest64);//same as decode(src.getBytes(StandardCharsets.ISO_8859_1))
                                    byte[] binres = Base64.getDecoder().decode(PResponse64);
                                    
                                    PRequestResponse pqr = new PRequestResponse(Host, Port, SSL, binreq, binres, enc);
                                    if(Disabled){
                                        pqr.Disable();
                                    }
                                    pqr.setComments(Comments);
                                    pqr.setError(Error);
                                    ReqResList.add(pqr);
                                    initReqRes();
                                }
                            }
                        }

                        break;
                    case END_ARRAY:
                        break;
                    default:
                        if(aparms!=null){
                            if(name.toUpperCase().equals("URL")){
                                aparms.setUrl(GetString(ev, value, ""));
                            }else if(name.toUpperCase().equals("LEN")){
                                aparms.len = GetNumber(ev, value, 0);
                            }else if(name.toUpperCase().equals("TYPEVAL")){
                                aparms.typeval = GetNumber(ev, value, 0);
                            }else if(name.toUpperCase().equals("INIVAL")){
                                aparms.inival = GetNumber(ev, value, 0);
                            }else if(name.toUpperCase().equals("MAXVAL")){
                                aparms.maxval = GetNumber(ev, value, 0);
                            }else if(name.toUpperCase().equals("CSVNAME")){
                                aparms.csvname = GetString(ev, value, "");
                            }else if(name.toUpperCase().equals("PAUSE")){
                                aparms.pause = Getboolean(ev,value, false);
                            }else if(name.toUpperCase().equals("TRACKFROMSTEP")){
                                aparms.setTrackFromStep(GetNumber(ev, value, 0));
                            }else if(name.toUpperCase().equals("SETTOSTEP")){
                                int stepno = GetNumber(ev, value, ParmVars.TOSTEPANY);
                                if(Version.isEmpty()){
                                    if(stepno <= 0){
                                        stepno = ParmVars.TOSTEPANY;
                                    }
                                }
                                aparms.setSetToStep(stepno);
                            }else if(name.toUpperCase().equals("RELATIVECNTFILENAME")){
                                aparms.setRelativeCntFileName(GetString(ev, value, ""));
                            }
                        }else if(current!=null&&current.toUpperCase().equals("PREQUESTRESPONSE")){
                            if(name.toUpperCase().equals("PREQUEST")){
                                PRequest64 = GetString(ev, value, "");
                            }else if(name.toUpperCase().equals("PRESPONSE")){
                                PResponse64 = GetString(ev,value, "");
                            }else if(name.toUpperCase().equals("HOST")){
                                Host = GetString(ev,value, "");
                            }else if(name.toUpperCase().equals("PORT")){
                                Port = GetNumber(ev,value, 0);
                            }else if(name.toUpperCase().equals("SSL")){
                                SSL = Getboolean(ev,value, false);
                            }else if(name.toUpperCase().equals("COMMENTS")){
                                Comments = GetString(ev,value, "");
                            }else if(name.toUpperCase().equals("DISABLED")){
                                Disabled = Getboolean(ev,value, false);
                            }else if(name.toUpperCase().equals("ERROR")){
                                Error = Getboolean(ev,value, false);
                            }
                        }else if(current!=null&&current.toUpperCase().equals("EXCLUDEMIMETYPES")){
                            if(!Version.isEmpty()){
                                addExcludeMimeType(GetString(ev, value, ""));
                            }
                        }
                        break;
                }

                break;
            case 2:
                switch(ev){
                    case START_OBJECT:
                        if(current!=null&&current.toUpperCase().equals("APPVALUE_LIST")){
                            //ParmVars.plog.debuglog(0, "START_OBJECT level2 name:" + current);
                            apv = new AppValue();
                        }
                        break;
                    case END_OBJECT:
                        if(!hasErrors()){
                            if(apv!=null&&aparms!=null){
                                aparms.addAppValue(apv);
                            }
                        }
                        apv = null;
                        break;
                    case END_ARRAY:
                        break;
                    default:
                        if(apv!=null){
                            if(name.toUpperCase().equals("VALPART")){
                                if(!apv.setValPart(GetString(ev, value, ""))){
                                    JSONSyntaxErrors.add("VALPART has no value:[" + value + "]");
                                }
                            }else if(name.toUpperCase().equals("ISMODIFY")){
                                if(Getboolean(ev, value, true)==false){
                                    apv.setEnabled(false);
                                }
                            }else if(name.toUpperCase().equals("ISENABLED")){
                                if(Getboolean(ev, value, true)==false){
                                    apv.setEnabled(false);
                                }
                            }else if(name.toUpperCase().equals("ISNOCOUNT")){
                                if(Getboolean(ev, value, true)==true){
                                    apv.setNoCount();
                                }else{
                                    apv.clearNoCount();
                                }
                            }else if(name.toUpperCase().equals("CSVPOS")){
                                apv.csvpos = GetNumber(ev, value, 0);
                            }else if(name.toUpperCase().equals("VALUE")){
                                if(!apv.setURLencodedVal(GetString(ev, value, ""))){
                                    JSONSyntaxErrors.add("Invalid VALUE :[" + value + "]");
                                }
                            }else if(name.toUpperCase().equals("RESURL")){
                                apv.setresURL(GetString(ev, value, ""));
                            }else if(name.toUpperCase().equals("RESREGEX")){
                                apv.setresRegexURLencoded(GetString(ev, value, ""));
                            }else if(name.toUpperCase().equals("RESVALPART")){
                                apv.setresPartType(GetString(ev, value, ""));
                            }else if(name.toUpperCase().equals("RESREGEXPOS")){
                                apv.resRegexPos = GetNumber(ev, value, 0);
                            }else if(name.toUpperCase().equals("TOKEN")){
                                apv.token = GetString(ev, value, "");
                            }else if(name.toUpperCase().equals("URLENCODE")){
                                apv.urlencode = Getboolean(ev, value, false);
                            }else if(name.toUpperCase().equals("FROMSTEPNO")){
                                apv.fromStepNo = GetNumber(ev, value, -1);
                            }else if(name.toUpperCase().equals("TOSTEPNO")){
                                int stepno = GetNumber(ev, value, ParmVars.TOSTEPANY);
                                if(Version.isEmpty()){
                                    if(stepno<=0){
                                        stepno = ParmVars.TOSTEPANY;
                                    }
                                }
                                apv.toStepNo = stepno;
                            }else if(name.toUpperCase().equals("TOKENTYPE")){
                                apv.setTokenTypeName(GetString(ev, value, ""));
                            }else if(name.toUpperCase().equals("RESENCODETYPE")){
                            	apv.setResEncodeType(GetString(ev, value, ""));
                            }
                        }
                        break;
                }
                break;
            default:
                break;
        }

        
        return !hasErrors();

    }

        public void addExcludeMimeType(String exttype){
            ExcludeMimeTypes.add(exttype);
        }
        
        public List<String> getExcludeMimeTypes(){
            return ExcludeMimeTypes;
        }
        
        
}
