/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burp;
import java.util.Objects;

/**
 *
 * @author daike
 */
public class ParmGenTokenKey {
    private AppValue.TokenTypeNames tokentype;
    private int fcnt;
    private String name;
    
   
    ParmGenTokenKey(AppValue.TokenTypeNames _tokentype, String _name, int _fcnt){
        tokentype = _tokentype;
        name = new String(_name);
        fcnt = _fcnt;
    }
    
    ParmGenTokenKey(ParmGenTokenKey tk){
    	tokentype = tk.tokentype;
    	name = new String(tk.name);
    	fcnt = tk.fcnt;
    }
    
    public String GetName(){
        return name;
    }
    
    public AppValue.TokenTypeNames GetTokenType(){
        return tokentype;
    }
    
    public int GetFcnt(){
        return fcnt;
    }
    
    public void SetTokenType(AppValue.TokenTypeNames _tktype){
    	tokentype = _tktype;
    }
    
    // HashMap
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ParmGenTokenKey) {
            ParmGenTokenKey key = (ParmGenTokenKey) obj;
            return this.tokentype == key.tokentype && this.name.toLowerCase().equals(key.name.toLowerCase()) && this.fcnt == key.fcnt;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(tokentype, name.toLowerCase(),fcnt);
    }
}
