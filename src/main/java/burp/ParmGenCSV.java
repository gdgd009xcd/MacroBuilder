/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package burp;

import flex.messaging.util.URLEncoder;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.json.stream.*;


/**
 *
 * @author daike
 */
public class ParmGenCSV {
    String lang;
    ArrayList<AppParmsIni> records;
    Iterator<AppParmsIni> it;
    ParmGenWriteFile pfile;
    public static ArrayList<PRequestResponse> selected_messages;
    public static ArrayList<PRequestResponse> proxy_messages;
    
    ParmGenCSV(ParmGenMacroTrace _pmt, String _lang, ArrayList<PRequestResponse> _selected_messages){
       setLang(_lang);
       reloadParmGen(_pmt);
       selected_messages = new ArrayList<PRequestResponse>();
       proxy_messages = _selected_messages;
       selected_messages.add(proxy_messages.get(0));
       pfile = null;

    }
    
    public void reloadParmGen(ParmGenMacroTrace _pmt){
       ParmGen pgen = new ParmGen(_pmt);
       records = pgen.parmcsv;
       if (records==null){
           records = new ArrayList<AppParmsIni>();
       }
       rewindAppParmsIni();
    }
    
    public void setParms(ArrayList<AppParmsIni> _records){
        records = _records;//reference
    }
    
    public void setLang(String _lang){
         if ( _lang == null || _lang.isEmpty()){
            lang = "UTF-8";
        }else{
            lang = _lang;
        }
    }
    
    public String getLang(){
        return lang;
    }
    


    public void add(String URL, String initval, String valtype, String incval, ArrayList<AppValue> apps){
        int rowcnt = records.size();
        records.add(new AppParmsIni(URL, initval, valtype, incval, apps, rowcnt));
    }

    public void add(AppParmsIni pini){
        records.add(pini);
    }
    
    public void mod(int i, String URL, String initval, String valtype, String incval, ArrayList<AppValue> apps){
        int rowcnt = records.get(i).getRow();
        records.set(i, new AppParmsIni(URL, initval, valtype, incval, apps, rowcnt));
    }
    
    public void mod(int i, AppParmsIni pini){
        records.set(i, pini);
    }
    
    public void del(int i){
        records.remove(i);
    }
    
    private String escapeDelimiters(String _d, String code) {
        //String _dd = _d.replaceAll("\\\\", "\\\\");
        String _dd = _d;
        //String _ddd = _dd.replaceAll("\"", "\"\"");
        String encoded = _d;
        try{
            if(code==null){
                code = ParmVars.enc;
            }
            encoded = URLEncoder.encode(_dd, code);
        }catch(UnsupportedEncodingException e){
            ParmVars.plog.printException(e);
            encoded = _dd;
        }
        return encoded;
    }
    
    private String QUOTE(String val, boolean comma){
        return "\"" + (val==null?"":val) + "\"" + ( comma ? "," : "" );
    }
    
    public void jsonsave(){
        //ファイル初期化
        try{
            pfile = new ParmGenWriteFile(ParmVars.parmfile + ".json");
        }catch(Exception ex){
            ParmVars.plog.printException(ex);
            return;
        }
        
        //JSON pretty print
        Map<String, Object> properties = new HashMap<>(1);
        properties.put(JsonGenerator.PRETTY_PRINTING, true);
        
        
        pfile.truncate();
        JsonObjectBuilder builder = Json.createObjectBuilder();
        
        builder.add("LANG", lang);
        
        if(ParmGen.ProxyInScope){
            builder.add("ProxyInScope", true);
        }else{
            builder.add("ProxyInScope", false);
        }
        if(ParmGen.IntruderInScope){
             builder.add("IntruderInScope", true);
        }else{
             builder.add("IntruderInScope", false);
        }
        if(ParmGen.RepeaterInScope){
            builder.add("RepeaterInScope", true);
        }else{
            builder.add("RepeaterInScope", false);
        }
        if(ParmGen.ScannerInScope){
            builder.add("ScannerInScope", true);
        }else{
            builder.add("ScannerInScope", false);
        }
        
        JsonArrayBuilder AppParmsIni_List =Json.createArrayBuilder();
        
        
        
        Iterator<AppParmsIni> it = records.iterator();
        while(it.hasNext()){
            AppParmsIni prec = it.next();
            //String URL, String initval, String valtype, String incval, ArrayList<ParmGenParam> parms
            JsonObjectBuilder AppParmsIni_prec = Json.createObjectBuilder();
            AppParmsIni_prec.add("URL", prec.url);
            AppParmsIni_prec.add("len", prec.len);
            AppParmsIni_prec.add("typeval", prec.typeval);
            AppParmsIni_prec.add("inival", prec.inival);
            AppParmsIni_prec.add("maxval", prec.maxval);
            AppParmsIni_prec.add("csvname", prec.typeval==AppParmsIni.T_CSV?escapeDelimiters(prec.frl.getFileName(), "UTF-8"):"");
            AppParmsIni_prec.add("enabled", true);
            
            JsonArrayBuilder AppValue_List =Json.createArrayBuilder();
            
            Iterator<AppValue> pt = prec.parmlist.iterator();
            String paramStr = "";
            while(pt.hasNext()){
                AppValue param = pt.next();
                JsonObjectBuilder AppValue_rec = Json.createObjectBuilder();
                
                AppValue_rec.add("valpart", param.getValPart());
                AppValue_rec.add("isModify", param.isModify());
                AppValue_rec.add("isNoCount", param.isNoCount());
                AppValue_rec.add("csvpos", param.csvpos);
                AppValue_rec.add("value", escapeDelimiters(param.value, null));               
                AppValue_rec.add("resURL", param.resURL==null?"":param.resURL);
                AppValue_rec.add("resRegex", (escapeDelimiters(param.resRegex, null)==null?"":escapeDelimiters(param.resRegex, null)));
                AppValue_rec.add("resValpart", param.getResValPart());
                AppValue_rec.add("resRegexPos", param.resRegexPos);
                AppValue_rec.add("token", param.token==null?"":param.token);
                AppValue_rec.add("urlencode", param.urlencode);
                AppValue_List.add(AppValue_rec);
            }
            
            AppParmsIni_prec.add("AppValue_List", AppValue_List);
            
            AppParmsIni_List.add(AppParmsIni_prec);
            
 
        }
        
        builder.add("AppParmsIni_List", AppParmsIni_List);
        JsonObject model = builder.build();
         
        //StringWriter stWriter = new StringWriter();
        //JsonWriter jsonWriter = Json.createWriter(stWriter);
        JsonWriter jsonWriter = Json.createWriterFactory(properties).createWriter(pfile.getPrintWriter());
        jsonWriter.writeObject(model);
        jsonWriter.close();

        //String jsonData = stWriter.toString();
    
        //pfile.print(jsonData);
        
        pfile.close();
        pfile = null;
    }
    
    public void save(){
        //ファイル初期化
        try{
            pfile = new ParmGenWriteFile(ParmVars.parmfile);
        }catch(Exception ex){
            ParmVars.plog.printException(ex);
            return;
        }
        
        pfile.truncate();
        String scopelist = new String();
        if(ParmGen.ProxyInScope){
            scopelist = "1:";
        }else{
            scopelist = "0:";
        }
        if(ParmGen.IntruderInScope){
            scopelist += "1:";
        }else{
            scopelist += "0:";
        }
        if(ParmGen.RepeaterInScope){
            scopelist += "1:";
        }else{
            scopelist += "0:";
        }
        if(ParmGen.ScannerInScope){
            scopelist += "1";
        }else{
            scopelist += "0";
        }
        
            
        pfile.print("LANG," + lang + "," + scopelist);
        //pfile.print("");
        
        Iterator<AppParmsIni> it = records.iterator();
        while(it.hasNext()){
            AppParmsIni prec = it.next();
            //String URL, String initval, String valtype, String incval, ArrayList<ParmGenParam> parms
            Iterator<AppValue> pt = prec.parmlist.iterator();
            String paramStr = "";
            while(pt.hasNext()){
                AppValue param = pt.next();
                if (!paramStr.isEmpty()){
                    paramStr += ",";
                }
                paramStr += QUOTE(param.getValPart() + (param.isModify()?"":"-") + (param.isNoCount()?"":"+") +
                        (param.csvpos == -1?"":(":" +Integer.toString(param.csvpos))) 
                        , true) ;
                if (prec.typeval != AppParmsIni.T_TRACK){
                        paramStr += QUOTE(escapeDelimiters(param.value, null), false);
                }else{
                    paramStr += QUOTE(escapeDelimiters(param.value, null), true) +
                        QUOTE(param.resURL, true) +
                        QUOTE(escapeDelimiters(param.resRegex, null), true) +
                        QUOTE(param.getResValPart(), true) +
                        QUOTE(Integer.toString(param.resRegexPos), true)+
                            QUOTE(param.token, true) +
                            QUOTE(param.urlencode==true?"true":"false", false);
                }

                        
            }
            pfile.print(QUOTE(prec.url, true) +
                    QUOTE(Integer.toString(prec.len), true) +
                    QUOTE(prec.getTypeVal(), true) +
                    (prec.typeval==AppParmsIni.T_CSV?QUOTE(escapeDelimiters(prec.frl.getFileName(), "UTF-8"), true):QUOTE(Integer.toString(prec.inival), true)) +
                    paramStr
                    );
            //pfile.print("");
        }
        
        pfile.close();
        pfile = null;
        
    }
   
    public AppParmsIni getAppParmsIni(int i){
        if ( records.size() > i){
            return records.get(i);
        }
        return null;
    }
    
    public void rewindAppParmsIni(){
        it = records.iterator();
    }
    
    public AppParmsIni getNextAppParmsIni(){
        if(it.hasNext()){
            return it.next();
        }
        return null;
    }
    
    public int sizeAppParmsIni(){
        return records.size();
    }
    
}
