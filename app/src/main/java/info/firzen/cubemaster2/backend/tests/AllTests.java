package info.firzen.cubemaster2.backend.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ CubeTest.class, FieldTest.class, PointTest.class,
		UsefulTest.class })
public class AllTests {

}
