package com.base.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.iii360.base.common.utl.LogManager;





/**
 * @author rtygbwwwerr
 *
 */
public class CommandInfo implements Serializable {
    
    private static final int INDEX_ARG = 4;
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public transient int _sessionId;
    public String _packageName;
    public String _commandName;
    public String _question;
    public String _answer;
    public String _appId;
    public boolean _isFromNet;
    
    
    /**
     * @hide
     */
    ArrayList<String> _argList = new ArrayList<String>();
    
    public CommandInfo() {
        
    }
    
    /**
     * @param msg
     */
//    public CommandInfo(MessageInfo msg) {
//        if(msg != null) {
//            _sessionId = msg._sessionId;
//            _commandName = msg._commandName;
//            _answer = msg._answer;
//            _question = msg._question;
//            _packageName = msg._packageName;
//            _argList = msg.getArgList();
//        }
//        
//    }
    
    public CommandInfo(String msg) {
        LogManager.d("Create commandInfo by:" + msg);
        if(msg != null) {
            String [] infos = msg.split("\\u007C\\u007C");
            for(String item : infos) {
                LogManager.i("field:" + item);
                
            }
            
            _commandName = infos[0];
            _question = infos[2];
            
            for(int i = INDEX_ARG;i < infos.length;i++) {
                _argList.add(infos[i]);
            }
        }
    }
    
    public void setArg(int index, String val) {
        if(_argList != null && _argList.size() > index) {
            _argList.set(index, val);
        }
    }
    
    public void setArgList(ArrayList<String> argList) {
        _argList = argList;
    }
    
    public void addArg(String arg) {
        if(_argList == null) {
            _argList = new ArrayList<String>();
        }
        _argList.add(arg);
    }
    
    public ArrayList<String> getArgList() {
        return _argList;
    }
    
    public String getArg(int index) {
        
        if(_argList != null && _argList.size() > index ) {
            return _argList.get(index);
        }
        
        return null;
    }
    
    @Override
    public String toString() {
        String msg = "";
        msg += format(_packageName);
        msg += format(_question);
        msg += format(_answer);
        msg += toCommandString();
        //split head and command data with "|||"
        //msg += "|";
        
        //msg += format(_url);

        return msg;
    }

    @Override
    public boolean equals(Object o) {
        
        if(o != null && o instanceof CommandInfo) {
            
            CommandInfo info = (CommandInfo) o;
            if(info._commandName.equals(_commandName)
            && _argList.equals(info._argList)) {
                return true;
            }
        }
        return false;
    }
    
    private String format(String input) {
        return input == null ? "||" : input + "||";
    }
    
//    public byte[] toCommandData() {
//        String msg = toString();
//       
//        byte [] data = Author.encryption(msg);
//        
//        return data;
//
//    }

	public void standardizing() {
        if(_commandName != null && _commandName.equals("CommandPhoneApi")) {
            String [] args = getArg(0).split("\\u007C\\u007C");
            _commandName = args[0];
            _argList.clear();
            for(int i = 1;i < args.length;i++) {
                if(args[i] != null && args[i].equals("\"\"")) {
                    args[i] = "";
                }
                _argList.add(args[i]);
            }
        }
		
	}
	
	public String toCommandString() {
        String data = "";
        data += format(_commandName);

        if(_argList != null) {
            for(String arg : _argList) {
            	data += format(arg);
            }
        }
		return data;
	}
}
