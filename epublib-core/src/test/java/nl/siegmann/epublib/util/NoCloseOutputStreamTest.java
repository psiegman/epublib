package nl.siegmann.epublib.util;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.OutputStream;

public class NoCloseOutputStreamTest {

	@Mock
	private OutputStream outputStream;

	private NoCloseOutputStream noCloseOutputStream;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.noCloseOutputStream = new NoCloseOutputStream(outputStream);
	}

    @Test
    public void testWrite() throws IOException {
        // given

        // when
        noCloseOutputStream.write(17);

        // then
        Mockito.verify(outputStream).write(17);
        Mockito.verifyNoMoreInteractions(outputStream);
    }

    @Test
    public void testClose() throws IOException {
	    // given

	    // when
		noCloseOutputStream.close();

	    // then
	    Mockito.verifyNoMoreInteractions(outputStream);
    }

	@Test
	public void testWriteClose() throws IOException {
		// given

		// when
		noCloseOutputStream.write(17);
		noCloseOutputStream.close();

		// then
		Mockito.verify(outputStream).write(17);
		Mockito.verifyNoMoreInteractions(outputStream);
	}
}
