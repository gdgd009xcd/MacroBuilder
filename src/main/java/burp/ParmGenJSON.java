/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burp;

import flex.messaging.util.URLDecoder;
import java.util.ArrayList;
import javax.json.stream.JsonParser;

/**
 *
 * @author chikara_1.daike
 */
public class ParmGenJSON {
    ArrayList<AppParmsIni> rlist;
    AppParmsIni aparms;
    AppValue apv;
    String exerr = null;
    int row = 0;
    
    ParmGenJSON(){
        
	rlist = new ArrayList<AppParmsIni>();
        aparms = null;
        apv = null;
        
    }
    
    ArrayList<AppParmsIni> Getrlist(){
        return rlist;
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
    
    boolean Parse(int alevel, JsonParser.Event ev, String name, Object value ){
       
        switch(alevel){
            case 0:
                if(name.toUpperCase().equals("LANG")){
                    ParmVars.enc = GetString(ev, value, "UTF-8");
                }else if(name.toUpperCase().equals("PROXYINSCOPE")){
                    ParmGen.ProxyInScope = Getboolean(ev, value, false);
                }else if(name.toUpperCase().equals("INTRUDERINSCOPE")){
                    ParmGen.IntruderInScope = Getboolean(ev,value, true);
                }else if(name.toUpperCase().equals("REPEATERINSCOPE")){
                    ParmGen.RepeaterInScope = Getboolean(ev, value, true);
                }else if(name.toUpperCase().equals("SCANNERINSCOPE")){
                    ParmGen.ScannerInScope = Getboolean(ev, value, true);
                }
                break;
            case 1:
                switch(ev){
                    case START_OBJECT:
                        aparms = new AppParmsIni();
                        aparms.parmlist = new ArrayList<AppValue>();
                        break;
                    case END_OBJECT:
                        if(exerr==null){
                            if(aparms!=null&&rlist!=null){
                                if(aparms.getType()==AppParmsIni.T_CSV){
                                    String decodedname = "";
                                    try{
                                            decodedname = URLDecoder.decode(aparms.csvname, "UTF-8");
                                    }catch(Exception e){
                                            ParmVars.plog.printException(e);
                                            exerr = e.getMessage();
                                    }
                                    aparms.frl = new FileReadLine(decodedname, true);
                                }
                                
                                aparms.setRowAndCntFile(row);row++;
                                aparms.crtGenFormat(true);
                                rlist.add(aparms);
                            }
                        }
                        aparms = null;
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
                            }
                        }
                        break;
                }
                
                break;
            case 2:
                switch(ev){
                    case START_OBJECT:
                        apv = new AppValue();
                        break;
                    case END_OBJECT:
                        if(exerr==null){
                            if(apv!=null&&aparms!=null){
                                apv.col = aparms.parmlist.size();
                                aparms.parmlist.add(apv);
                            }
                        }
                        apv = null;
                        break;
                    default:
                        if(apv!=null){
                            if(name.toUpperCase().equals("VALPART")){
                                apv.setValPart(GetString(ev, value, ""));
                            }else if(name.toUpperCase().equals("ISMODIFY")){
                                if(Getboolean(ev, value, true)==false){
                                    apv.setNoModify();
                                }
                            }else if(name.toUpperCase().equals("ISNOCOUNT")){
                                if(Getboolean(ev, value, true)==true){
                                    apv.setNoCount();
                                }
                            }else if(name.toUpperCase().equals("CSVPOS")){
                                apv.csvpos = GetNumber(ev, value, 0);
                            }else if(name.toUpperCase().equals("VALUE")){
                                exerr = apv.setURLencodedVal(GetString(ev, value, ""));
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
                                apv.toStepNo = GetNumber(ev, value, 0);
                            }
                        }
                        break;
                }
                break;
            default:
                break;
        }
        
        if(exerr==null){
            return true;
        }else{
            ParmVars.plog.printError("ParmGenJSON::Parse " + exerr);
        }
        return false;
        
    }
}