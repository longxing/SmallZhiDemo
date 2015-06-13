package com.voice.common.util;

import android.os.Parcel;
import android.os.Parcelable;

public class CommandInfo implements Parcelable {

	private String commandName;
	private String commandID;

	public CommandInfo() {
		
	}

	public CommandInfo(String commandName, String commandID) {
		this.commandName = commandName;
		this.commandID = commandID;

	}

	public static final Parcelable.Creator<CommandInfo> CREATOR = new Parcelable.Creator<CommandInfo>() {

		public CommandInfo createFromParcel(Parcel in) {
			return new CommandInfo(in);
		}

		public CommandInfo[] newArray(int size) {
			return new CommandInfo[size];
		}

	};

	private CommandInfo(Parcel in) {
		readFromParcel(in);
	}

	private void readFromParcel(Parcel in) {
		// TODO Auto-generated method stub
		commandName = in.readString();
		commandID = in.readString();
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(commandName);
		dest.writeString(commandID);

	}

	public String getCommandName() {
		return commandName;
	}

	public void setCommandName(String commandName) {
		this.commandName = commandName;
	}

	public String getCommandContent() {
		return commandID;
	}

	public void setCommandContent(String commandContent) {
		this.commandID = commandContent;
	}
}
