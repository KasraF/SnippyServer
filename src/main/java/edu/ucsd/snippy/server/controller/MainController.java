package edu.ucsd.snippy.server.controller;

import edu.ucsd.snippy.Snippy;
import edu.ucsd.snippy.server.responses.SynthResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController
{
	@Autowired
	public MainController(
		@Value("${snippy.script.runpy}") String runPyScript)
	{
		this.runPyScript = runPyScript;
	}

	@GetMapping({"/", ""})
	String title()
	{
		return "title";
	}

	@GetMapping({"/editor", "editor"})
	String editor()
	{
		return "editor";
	}

	@PostMapping("/synthesize")
	@ResponseBody
	SynthResult synthesize(@RequestBody String problem)
	{
		String rs;

		try {
			this.logger.debug(problem);
			rs = Snippy.synthesize(problem, SYNTH_DURATION)
					._1()
					.getOrElse(null);
			this.logger.debug(rs);
		} catch (Exception e) {
			// Catch all to make sure the front-end is updated.
			LoggerFactory.getLogger(this.getClass()).error("Synthesis failed.", e);
			rs = null;
		}

		if (rs != null) {
			this.logger.debug("Synthesized: " + rs);
		} else {
			this.logger.debug("Synthesis failed.");
		}

		return new SynthResult(0, rs != null, rs);
	}

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public static final int SYNTH_DURATION = 7;

	protected final String runPyScript;
}
