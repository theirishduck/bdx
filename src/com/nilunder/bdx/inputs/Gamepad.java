package com.nilunder.bdx.inputs;

import java.util.*;
import javax.vecmath.*;

import com.badlogic.gdx.controllers.*;
import com.badlogic.gdx.utils.*;

import com.nilunder.bdx.utils.Named;

public class Gamepad implements Named {

public static class Axis{
	public int code;
	public float deadZone;
	public float value; 

	public Axis(int code, float deadZone){
		this.code = code;
		this.deadZone = deadZone;
	}

	public Axis(int code){
		this(code, 0.25f);
	}
}

public static class Stick{
	public Axis x;
	public Axis y;

	public Stick(Axis x, Axis y){
		this.x = x;
		this.y = y;
	}

	public Vector3f pos(){
		return new Vector3f(x.value, -y.value, 0);
	}

	public void deadZone(float dz){
		x.deadZone = dz;
		y.deadZone = dz;
	}
}

public static class Profile{

	public static class FnProcessAxis{
		public float[] eval(int axis, float value){
			return null;
		}
	}

	public FnProcessAxis processAxis;

	public String name;
	public HashMap<String,Integer> btnToCode;
	public HashMap<Integer,GdxProcessor.UpDownLog> codeToLog;

	public HashMap<String,Axis> axes;
	public HashMap<Integer,Axis> codeToAxis;

	public HashMap<String,Stick> sticks;

	public Profile(String name){
		this.name = name;
		btnToCode = new HashMap<String,Integer>();
		axes = new HashMap<String,Axis>();
		sticks = new HashMap<String,Stick>();
	}

	public GdxProcessor.UpDownLog btnLog(String btn){
		return codeToLog.get(btnToCode.get(btn));
	}
}

	public Controller controller;
	public Profile profile;
	public HashMap<String,Axis> axes;
	public HashMap<String,Stick> sticks;

	public HashMap<String,Profile> profiles;
	private int index;

	public Gamepad(int i){

		index = i;

		Array controllers = Controllers.getControllers();

		if (controllers.size > index)
			controller = (Controller)controllers.get(index);
		else
			throw new IndexOutOfBoundsException("ERROR: There is no gamepad connected at index " + index);

		controller.addListener(new GdxProcessor.GamepadAdapter(this));

		profiles = new HashMap<String,Profile>();

		Profile p = new Profile("XBOX");

		p.btnToCode.put("x", 3);
		p.btnToCode.put("y", 4);
		p.btnToCode.put("a", 0);
		p.btnToCode.put("b", 1);
		p.btnToCode.put("white", 5);
		p.btnToCode.put("black", 2);
		p.btnToCode.put("back", 6);
		p.btnToCode.put("start", 7);
		p.btnToCode.put("rs", 8);
		p.btnToCode.put("ls", 9);

		p.axes.put("lx", new Axis(0));
		p.axes.put("ly", new Axis(1));
		p.axes.put("rx", new Axis(3));
		p.axes.put("ry", new Axis(4));
		p.axes.put("lt", new Axis(2));
		p.axes.put("rt", new Axis(5));

		p.sticks.put("left", new Stick(p.axes.get("lx"), p.axes.get("ly")));
		p.sticks.put("right", new Stick(p.axes.get("rx"), p.axes.get("ry")));

		// Each profile has a processAxis reference, which can be
		// set to a new FnProcessAxis function object, to convert
		// incoming axis codes and values before they're actually set. 
		// Here, we use it to convert awkward xbox trigger axis values 
		// (-1 when released, 1 when fully pressed) to something more
		// sensible (0 when released, 1 when fully pressed):
		//
		p.processAxis = new Profile.FnProcessAxis(){
			public float[] eval(int axis, float value){

				if (axis == 2 || axis == 5){ // "LT" or "RT"
					value = (value + 1) / 2;
				}

				return new float[]{axis, value};
			}
		};

		// The system will "buttonize" the dpad to 1XX button codes,
		// which can be mapped like this:
		//
		p.btnToCode.put("left", 100 + PovDirection.west.ordinal());
		p.btnToCode.put("right", 100 + PovDirection.east.ordinal());
		p.btnToCode.put("up", 100 + PovDirection.north.ordinal());
		p.btnToCode.put("down", 100 + PovDirection.south.ordinal());

		// Similarly for available axes, but with +/- 2XX button codes:
		//
		p.btnToCode.put("ls-left", -200 - p.axes.get("lx").code);
		p.btnToCode.put("ls-right", 200 + p.axes.get("lx").code);
		p.btnToCode.put("ls-up", -200 - p.axes.get("ly").code);
		p.btnToCode.put("ls-down", 200 + p.axes.get("ly").code);

		p.btnToCode.put("rs-left", -200 - p.axes.get("rx").code);
		p.btnToCode.put("rs-right", 200 + p.axes.get("rx").code);
		p.btnToCode.put("rs-up", -200 - p.axes.get("ry").code);
		p.btnToCode.put("rs-down", 200 + p.axes.get("ry").code);

		p.btnToCode.put("rt", 200 + p.axes.get("rt").code);
		p.btnToCode.put("lt", 200 + p.axes.get("lt").code);

		profiles.put(p.name, p);

		p = new Profile("XBOX360");

		p.btnToCode = new HashMap<String,Integer>(profiles.get("XBOX").btnToCode);
		p.btnToCode.remove("white");
		p.btnToCode.remove("black");

		p.btnToCode.put("x", 2);
		p.btnToCode.put("y", 3);
		p.btnToCode.put("lb", 4);
		p.btnToCode.put("rb", 5);

		p.btnToCode.put("ls", 8);
		p.btnToCode.put("rs", 9);

		if (SharedLibraryLoader.isLinux) {
			p.btnToCode.put("ls", 9);
			p.btnToCode.put("rs", 10);
		}

		p.axes.put("lx", new Axis(1));
		p.axes.put("ly", new Axis(0));

		if (SharedLibraryLoader.isLinux) {
			p.axes.put("lx", new Axis(0));
			p.axes.put("ly", new Axis(1));
		}

		p.axes.put("rx", new Axis(3));
		p.axes.put("ry", new Axis(2));

		if (SharedLibraryLoader.isLinux) {
			p.axes.put("rx", new Axis(3));
			p.axes.put("ry", new Axis(4));
		}

		p.axes.put("lt", new Axis(4));
		p.axes.put("rt", new Axis(5));

		if (SharedLibraryLoader.isLinux) {
			p.axes.put("lt", new Axis(2));
			p.axes.put("rt", new Axis(5));
		}

		p.sticks.put("left", new Stick(p.axes.get("lx"), p.axes.get("ly")));
		p.sticks.put("right", new Stick(p.axes.get("rx"), p.axes.get("ry")));

		p.btnToCode.put("ls-left", -200 - p.axes.get("lx").code);
		p.btnToCode.put("ls-right", 200 + p.axes.get("lx").code);
		p.btnToCode.put("ls-up", -200 - p.axes.get("ly").code);
		p.btnToCode.put("ls-down", 200 + p.axes.get("ly").code);

		p.btnToCode.put("rs-left", -200 - p.axes.get("rx").code);
		p.btnToCode.put("rs-right", 200 + p.axes.get("rx").code);
		p.btnToCode.put("rs-up", -200 - p.axes.get("ry").code);
		p.btnToCode.put("rs-down", 200 + p.axes.get("ry").code);

		p.btnToCode.put("rt", 200 + p.axes.get("rt").code);
		p.btnToCode.put("lt", 200 + p.axes.get("lt").code);

		// Unlike the original xbox, which uses one axis per trigger, 
		// xbox360 gamepads use a single axis for both triggers, where RT
		// values are positive, while LT values are negative. We convert 
		// to effectively place RT on a different axis index (5), which enables us 
		// to use positive values for both triggers.
		// 
		p.processAxis = new Profile.FnProcessAxis(){

			public float[] eval(int axis, float value){

				if (SharedLibraryLoader.isWindows) {
					if (axis == 4 && value < 0) {
						axis = 5; // RT
						value = -value;
					}
				}
				else if ((SharedLibraryLoader.isLinux) && (axis == axes.get("lt").code || axis == axes.get("rt").code)) {		// On Linux, each trigger goes from -1 (released) to 1 (fully pressed)
					value += 1;
					value /= 2;
					value = Math.min(Math.max(value, 0), 1);		// For some reason, the maximum range of the trigger grows when adding one to the value
				}
				return new float[]{axis, value};
			}

		};

		profiles.put(p.name, p);

		p = new Profile("Generic");

		for (int a = 0; a < 32; a++)
			p.btnToCode.put("b" + String.valueOf(a), a);

		for (int a = 0; a < 16; a++) {
			String axisName = "a" + String.valueOf(a);
			Axis axis = new Axis(a);
			p.axes.put(axisName, axis);
			p.btnToCode.put(axisName + "-", -200 - axis.code);
			p.btnToCode.put(axisName + "+", 200 + axis.code);
		}
		
		for (int a = 0; a < 8; a++) {
			Stick s = new Stick(p.axes.get("a" + String.valueOf(a * 2)),
					p.axes.get("a" + String.valueOf(a * 2 + 1)));
			p.sticks.put("s" + String.valueOf(a), s);
		}

		p.btnToCode.put("left", 100 + PovDirection.west.ordinal());
		p.btnToCode.put("right", 100 + PovDirection.east.ordinal());
		p.btnToCode.put("up", 100 + PovDirection.north.ordinal());
		p.btnToCode.put("down", 100 + PovDirection.south.ordinal());

		profiles.put(p.name, p);

		profile("XBOX360"); // probably most common, so it's the default

	}

	public void profile(String name){
		profile = profiles.get(name);
		if (profile.codeToLog == null){
			profile.codeToLog = new HashMap<>();
			for (Integer code : profile.btnToCode.values())
				profile.codeToLog.put(code, new GdxProcessor.UpDownLog());

			profile.codeToAxis = new HashMap<>();
			for (Axis axis : profile.axes.values())
				profile.codeToAxis.put(axis.code, axis);

			axes = profile.axes;
			sticks = profile.sticks;
		}
	}

	public boolean btnHit(String btn){
		GdxProcessor.UpDownLog b = profile.btnLog(btn);
		return b.hit == GdxProcessor.currentTick;
	}

	public boolean btnDown(String btn){
		GdxProcessor.UpDownLog b = profile.btnLog(btn);
		return b.hit > b.up;
	}

	public boolean btnUp(String btn){
		GdxProcessor.UpDownLog b = profile.btnLog(btn);
		return b.up == GdxProcessor.currentTick;
	}

	public int index(){
		return index;
	}

	public String name(){
		return controller.getName();
	}

	public String toString(){
		return name();
	}

	public ArrayList<Integer> downButtons() {
		ArrayList<Integer> buttons = new ArrayList<Integer>();
		for (int index : profile.btnToCode.values()) {
			if (controller.getButton(index))
				buttons.add(index);
		}
		return buttons;
	}

	public ArrayList<ArrayList<Integer>> downAxes(float deadZone){
		ArrayList<ArrayList<Integer>> axes = new ArrayList<ArrayList<Integer>>();
		for (Axis axis : profile.axes.values()) {
			if (Math.abs(axis.value) > deadZone) {
				ArrayList<Integer> ar = new ArrayList<Integer>();
				ar.add(axis.code);
				ar.add((int) Math.signum(axis.value));
				axes.add(ar);
			}
		}
		return axes;
	}

	public ArrayList<ArrayList<Integer>> downAxes() {
		return downAxes(0.2f);
	}

	public ArrayList<String> downInputs(){
		ArrayList<String> inputs = new ArrayList<String>();
		if (profile != null) {
			for (String s : profile.btnToCode.keySet()) {
				if (btnDown(s))
					inputs.add(s);
			}
		}
		return inputs;
	}

	public ArrayList<String> hitInputs(){
		ArrayList<String> inputs = new ArrayList<String>();
		if (profile != null) {
			for (String s : profile.btnToCode.keySet()) {
				if (btnHit(s))
					inputs.add(s);
			}
		}
		return inputs;
	}

}
