package nl.siegmann.epublib.util;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.Writer;

public class NoCloseWriterTest {

	@Mock
	private Writer delegateWriter;

	private NoCloseWriter noCloseWriter;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.noCloseWriter = new NoCloseWriter(delegateWriter);
	}

    @Test
    public void testWrite() throws IOException {
        // given

        // when
        noCloseWriter.write(new char[]{'e','f','g'},2,1);

        // then
        Mockito.verify(delegateWriter).write(new char[]{'e','f','g'},2,1);
        Mockito.verifyNoMoreInteractions(delegateWriter);
    }

    @Test
    public void testFlush() throws IOException {
	    // given

	    // when
		noCloseWriter.flush();

	    // then
	    Mockito.verify(delegateWriter).flush();
	    Mockito.verifyNoMoreInteractions(delegateWriter);
    }

	@Test
	public void testClose() throws IOException {
		// given

		// when
		noCloseWriter.close();

		// then
		Mockito.verifyNoMoreInteractions(delegateWriter);
	}

	@Test
	public void testWriteClose() throws IOException {
		// given

		// when
		noCloseWriter.write(new char[]{'e','f','g'},2,1);
		noCloseWriter.close();

		// then
		Mockito.verify(delegateWriter).write(new char[]{'e','f','g'},2,1);
		Mockito.verifyNoMoreInteractions(delegateWriter);
	}
}
