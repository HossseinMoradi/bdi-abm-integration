/**
 *
 */
package io.github.agentsoz.ees;

import io.github.agentsoz.util.TestUtils;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.apache.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;
import org.matsim.core.utils.misc.CRCChecksum;
import org.matsim.testcases.MatsimTestUtils;

/**
 * @author dsingh, Joel Robertson
 *
 */
public class AngleseaBase12kTest {

    // have tests in separate classes so that they run, at least under maven, in separate JVMs.  kai, nov'17

    private static final Logger log = Logger.getLogger(AngleseaBase12kTest.class);
    @Ignore
    @Rule
    public MatsimTestUtils utils = new MatsimTestUtils();

    @Test
    public void AngleseaBase12kTest() {

        String[] args = {
                "--config", "scenarios/surf-coast-shire/anglesea-base/scenario_main.xml",
                "--logfile", "scenarios/surf-coast-shire/anglesea-base/scenario.log",
                "--loglevel", "INFO",
                //	                "--plan-selection-policy", "FIRST", // ensures it is deterministic, as default is RANDOM
                "--seed", "12345",
                "--safeline-output-file-pattern", "scenarios/surf-coast-shire/anglesea-base/safeline.%d%.out",
                "--matsim-output-directory", utils.getOutputDirectory(),
                "--jillconfig", "--config={" +
                "agents:[{classname:io.github.agentsoz.ees.agents.Resident, args:null, count:12000}]," +
                "logLevel: WARN," +
                "logFile: \"scenarios/surf-coast-shire/anglesea-base/jill.log\"," +
                "programOutputFile: \"scenarios/surf-coast-shire/anglesea-base/jill.out\"," +
                "randomSeed: 12345," + // jill random seed
                "numThreads: 1"+ // run jill in single-threaded mode so logs are deterministic
                "}"};

        Main.main(args);

//        final String actualEventsFilename = utils.getOutputDirectory() + "/output_events.xml.gz";
//        long actualEventsCRC = CRCChecksum.getCRCFromFile( actualEventsFilename ) ;
//        System.err.println("actual(events)="+actualEventsCRC) ;
//
//        long actualPlansCRC = CRCChecksum.getCRCFromFile( utils.getOutputDirectory() + "/output_plans.xml.gz" ) ;
//        System.err.println("actual(plans)="+actualPlansCRC) ;
//
//        // ---
//
//        final String primaryExpectedEventsFilename = utils.getInputDirectory() + "/output_events.xml.gz";
//
//        // ---
//
//        TestUtils.comparingDepartures(primaryExpectedEventsFilename,actualEventsFilename,300.);
//        TestUtils.comparingArrivals(primaryExpectedEventsFilename,actualEventsFilename,300.);
//        TestUtils.comparingActivityStarts(primaryExpectedEventsFilename,actualEventsFilename, 300.);
//        TestUtils.compareFullEvents(primaryExpectedEventsFilename,actualEventsFilename, false);
    }
}

