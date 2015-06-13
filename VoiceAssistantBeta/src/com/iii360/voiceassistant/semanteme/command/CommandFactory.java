package com.iii360.voiceassistant.semanteme.command;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.base.inf.parse.IVoiceCommand;

import android.content.Context;

public abstract class CommandFactory {
	private static final String TAG = "CommandFactory";
	private static final String METHOD_TAG = "createCommand";

	private CommandFactory() {
		
	}

	public static <T> IVoiceCommand createCommand(Class<? extends IVoiceCommand> cls, BasicServiceUnion union, T arg) {
		IVoiceCommand newCmd = null;
		Constructor<? extends IVoiceCommand> constructor;
		try {
			constructor = cls.getConstructor(new Class[] { BasicServiceUnion.class, arg.getClass() });
			newCmd = constructor.newInstance(new Object[] { union, arg, });
		} catch (SecurityException e) {
			LogManager.printStackTrace(e, TAG, METHOD_TAG);
		} catch (NoSuchMethodException e) {
			LogManager.printStackTrace(e, TAG, METHOD_TAG);
		} catch (IllegalArgumentException e) {
			LogManager.printStackTrace(e, TAG, METHOD_TAG);
		} catch (InstantiationException e) {
			LogManager.printStackTrace(e, TAG, METHOD_TAG);
		} catch (IllegalAccessException e) {
			LogManager.printStackTrace(e, TAG, METHOD_TAG);
		} catch (InvocationTargetException e) {
			LogManager.printStackTrace(e, TAG, METHOD_TAG);
		}
		return newCmd;
	}

	@SuppressWarnings("unchecked")
	public static <T> IVoiceCommand createCommand(String commandName, BasicServiceUnion union, T arg) {
		IVoiceCommand command = null;
		Class<? extends IVoiceCommand> cls;
		try {
			cls = (Class<? extends IVoiceCommand>) Class.forName(IVoiceCommand.COMMAND_HEAD + commandName);
			LogManager.d("Create command:" + commandName);
			command = createCommand(cls, union, arg);
		} catch (ClassNotFoundException e) {
			// command = new CommandHandleError(sessionId, handler, context,
			// (Matcher) null);
			LogManager.printStackTrace(e);
		}

		return command;

	}
}
