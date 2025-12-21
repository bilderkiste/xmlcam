package model;

public class Environment {
	
	private Settings settings;
	private GCodeDialect dialect;
	private Program program;
	
	public Environment(Settings settings, GCodeDialect dialect, Program program) {
		this.settings = settings;
		this.dialect = dialect;
		this.program = program;
	}

	public Settings getSettings() {
		return settings;
	}

	public GCodeDialect getDialect() {
		return dialect;
	}

	public Program getProgram() {
		return program;
	}
	
}
