// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.io.FileUtil;
import jodd.util.StringUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class MalformedTest {

	protected String testDataRoot;

	@Before
	public void setUp() throws Exception {
		if (testDataRoot != null) {
			return;
		}
		URL data = NodeSelectorTest.class.getResource("test");
		testDataRoot = data.getFile();
	}

	@Test
	public void testOneNode() {
		String content = "<body><div>test<span>sss</span></body>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<body><div>test<span>sss</span></div></body>", doc.getHtml());
		assertTrue(doc.check());
	}

	@Test
	public void testOneNodeWithBlanks() {
		String content = "<body><div>   <span>sss</span></body>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<body><div>   <span>sss</span></div></body>", doc.getHtml());
		assertTrue(doc.check());
	}

	@Test
	public void testTwoNodes() {
		String content = "<body><div>test<span><form>xxx</form></body>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<body><div>test<span><form>xxx</form></span></div></body>", doc.getHtml());
		assertTrue(doc.check());
	}

	@Test
	public void testTwoNodes2() {
		String content = "<body><div>test<span><form>xxx</body>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<body><div>test<span><form>xxx</form></span></div></body>", doc.getHtml());
		assertTrue(doc.check());
	}

	@Test
	public void testPeterSimple1() {
		String content = "<div><h1>FORELE</h1><p>dicuss<div>xxx</div></div>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<div><h1>FORELE</h1><p>dicuss</p><div>xxx</div></div>", doc.getHtml());
		assertTrue(doc.check());
	}

	@Test
	public void testPeterSimple2() {
		String content = "<div><h1>FORELE</h1><p>dicuss<div><h2>HAB</h2><p>AMONG</div></div>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<div><h1>FORELE</h1><p>dicuss</p><div><h2>HAB</h2><p>AMONG</p></div></div>", doc.getHtml());
		assertTrue(doc.check());
	}

	@Test
	public void testPeterSimple3WithSpaces() {
		String content = "<div> <h1>FORELE</h1> <p>dicuss <div> <h2>HAB</h2> <p>AMONG </div> </div>".toUpperCase();
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<div> <h1>FORELE</h1> <p>DICUSS </p><div> <h2>HAB</h2> <p>AMONG </p></div> </div>", doc.getHtml());
		assertTrue(doc.check());
	}

	@Test
	public void testPeterFull() {
		String content = "<DIV class=\"section\" id=\"forest-elephants\" >\n" +
				"<H1>Forest elephants</H1>\n" +
				"<P>In this section, we discuss the lesser known forest elephants.\n" +
				"...this section continues...\n" +
				"<DIV class=\"subsection\" id=\"forest-habitat\" >\n" +
				"<H2>Habitat</H2>\n" +
				"<P>Forest elephants do not live in trees but among them.\n" +
				"...this subsection continues...\n" +
				"</DIV>\n" +
				"</DIV>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);

		String expected = "<div class=\"section\" id=\"forest-elephants\">\n" +
				"<h1>Forest elephants</h1>\n" +
				"<p>In this section, we discuss the lesser known forest elephants.\n" +
				"...this section continues...\n</p>" +
				"<div class=\"subsection\" id=\"forest-habitat\">\n" +
				"<h2>Habitat</h2>\n" +
				"<p>Forest elephants do not live in trees but among them.\n" +
				"...this subsection continues...\n</p>" +
				"</div>\n" +
				"</div>";

		assertEquals(expected, doc.getHtml());
		assertTrue(doc.check());
	}

	@Test
	public void testEof() {
		String content = "<body><div>test";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<body><div>test</div></body>", doc.getHtml());
		assertTrue(doc.check());
	}

	@Test
	public void testEof2() {
		String content = "<body><div>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<body><div></div></body>", doc.getHtml());
		assertTrue(doc.check());
	}

	@Test
	public void testSpanDivOverTable() {
		String content = "<span><div><table><tr><td>text</span>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<span><div><table><tr><td>text</td></tr></table></div></span>", doc.getHtml());
		assertTrue(doc.check());
	}

	@Test
	public void testDivSpanOverTable() {
		String content = "<div><span><table><tr><td>text</div>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<div><span><table><tr><td>text</td></tr></table></span></div>", doc.getHtml());
		assertTrue(doc.check());
	}

	@Test
	public void testTableInTableInTable() throws IOException {
		String html = read("tableInTable.html", false);

		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(html);

		String out = read("tableInTable-out.html", true);

		assertEquals(out, html(doc));
		assertTrue(doc.check());
	}

	@Test
	public void testFormClosesAll() throws IOException {
		String html = read("formClosesAll.html", false);

		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.enableDebug();
		Document doc = lagartoDOMBuilder.parse(html);
		html = html(doc);

		String out = read("formClosesAll-out1.html", true);
		assertEquals(out, html);
		assertTrue(doc.check());

		lagartoDOMBuilder.setUseFosterRules(true);
		doc = lagartoDOMBuilder.parse(html);
		html = html(doc);

		out = read("formClosesAll-out2.html", true);
		assertEquals(out, html);
	}

	@Test
	public void testFoster1() {
		String html = "A<table>B<tr>C</tr>D</table>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.setUseFosterRules(true);
		Document doc = lagartoDOMBuilder.parse(html);
		html = html1(doc);

		assertEquals("ABCD<table><tr></tr></table>", html);
	}

	@Test
	public void testFoster2() {
		String html = "A<table><tr> B</tr> C</table>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.setUseFosterRules(true);
		Document doc = lagartoDOMBuilder.parse(html);
		html = html1(doc);

		assertEquals("ABC<table><tr></tr></table>", html);
	}

	@Test
	public void testBodyEnd() {
		String html = "<body><p>111</body>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.enableDebug();
		Document doc = lagartoDOMBuilder.parse(html);
		html = html1(doc);

		assertEquals("<body><p>111</p></body>", html);
		assertNull(doc.getErrors());
	}

	@Test
	public void testBodyEndWithError() {
		String html = "<body><p>111<h1>222</body>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.enableDebug();
		Document doc = lagartoDOMBuilder.parse(html);
		html = html1(doc);

		assertEquals("<body><p>111</p><h1>222</h1></body>", html);
		assertNotNull(doc.getErrors());
		assertEquals(1, doc.getErrors().size());
	}

	@Test
	public void testEOF() {
		String html = "<body><p>111";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.enableDebug();
		Document doc = lagartoDOMBuilder.parse(html);
		html = html1(doc);

		assertEquals("<body><p>111</p></body>", html);
		assertNull(doc.getErrors());
	}

	@Test
	public void testEOFWithError() {
		String html = "<body><p>111<h1>222";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.enableDebug();
		Document doc = lagartoDOMBuilder.parse(html);
		html = html1(doc);

		assertEquals("<body><p>111</p><h1>222</h1></body>", html);
		assertNotNull(doc.getErrors());
		assertEquals(1, doc.getErrors().size());
	}

	@Test
	public void testCrazySpan() throws IOException {
		String html = read("spancrazy.html", false);
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.enableHtmlPlusMode();
		lagartoDOMBuilder.enableDebug();

		Document doc = lagartoDOMBuilder.parse(html);
		html = html(doc);

		String out = read("spancrazy-out.html", true);
		assertEquals(out, html);
		assertEquals(3, doc.getErrors().size());
	}

	@Test
	public void testFosterForm() throws IOException {
		String html = read("fosterForm.html", false);
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.enableHtmlPlusMode();
		lagartoDOMBuilder.enableDebug();

		Document doc = lagartoDOMBuilder.parse(html);
		html = html(doc);

		String out = read("fosterForm-out.html", true);
		assertEquals(out, html);
		assertNull(doc.getErrors());
	}

	@Test
	public void testListCrazy() throws IOException {
		String html = read("listcrazy.html", false);
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.enableHtmlPlusMode();
		lagartoDOMBuilder.enableDebug();

		Document doc = lagartoDOMBuilder.parse(html);
		html = html(doc);

		String out = read("listcrazy-out.html", true);
		assertEquals(out, html);
		assertEquals(1, doc.getErrors().size());
	}

	@Test
	public void testTable1() throws IOException {
		String html = read("table1.html", false);
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.enableHtmlPlusMode();
		lagartoDOMBuilder.enableDebug();

		Document doc = lagartoDOMBuilder.parse(html);
		html = html(doc);

		String out = read("table1-out.html", true);
		assertEquals(out, html);
	}

	@Test
	public void testTable2() throws IOException {
		String html = read("table2.html", false);
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.enableHtmlPlusMode();
		lagartoDOMBuilder.enableDebug();

		Document doc = lagartoDOMBuilder.parse(html);
		html = html(doc);

		String out = read("table2-out.html", true);
		assertEquals(out, html);
	}

	// ---------------------------------------------------------------- util

	/**
	 * Reads test file and returns its content optionally stripped.
	 */
	protected String read(String filename, boolean strip) throws IOException {
		String data = FileUtil.readString(new File(testDataRoot, filename));
		if (strip) {
			data = StringUtil.removeChars(data, " \r\n\t");
			data = StringUtil.replace(data, ">", ">\n");
		}
		return data;
	}

	/**
	 * Parses HTML and returns the stripped html.
	 */
	protected String html(Document document) {
		String html = document.getHtml();
		html = StringUtil.removeChars(html, " \r\n\t");
		html = StringUtil.replace(html, ">", ">\n");
		return html;
	}
	protected String html1(Document document) {
		String html = document.getHtml();
		html = StringUtil.removeChars(html, " \r\n\t");
		return html;
	}

}