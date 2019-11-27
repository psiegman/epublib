package nl.siegmann.epublib.epub;

import nl.siegmann.epublib.domain.Book;

import java.io.IOException;

/**
 * Created by Juan Francisco Rodríguez <juan@bicubic.cl>
 **/
public class EpubWriterFactory {

    public static EpubWriter createWriter(Book book, BookProcessor bookProcessor) throws IOException {
        if(book.getEpubVersion().equals("3.0")) {
            return new EpubWriter3(bookProcessor);
        }
        if(book.getEpubVersion().equals("2.0")) {
            return new EpubWriter(bookProcessor);
        }
        return new EpubWriter(bookProcessor);
    }

}
