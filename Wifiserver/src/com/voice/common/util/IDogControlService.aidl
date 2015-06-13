package  com.voice.common.util;
import com.voice.common.util.CommandInfo; 
import com.voice.common.util.IChangeListener;


interface IDogControlService{
	List<CommandInfo> getCommand();
	
	boolean sendCommand(in String commandid);

	void setCommandChangeListen(in IChangeListener listen);

}