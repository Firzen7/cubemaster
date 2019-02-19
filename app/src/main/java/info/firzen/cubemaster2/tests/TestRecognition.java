package info.firzen.cubemaster2.tests;

import info.firzen.cubemaster2.recognition.Recognition;
import info.firzen.cubemaster2.recognition.Rgb;

import java.util.List;

import org.junit.Test;

public class TestRecognition {
	@Test
	public void test() {
		Recognition rec = new Recognition();
		
		List<Rgb> colors = rec.getCubeColors();
		List<Rgb> sorted = rec.sortColorsByDistance(new Rgb(75, 232, 40), colors);
		
		System.out.println(sorted);
	}
}
