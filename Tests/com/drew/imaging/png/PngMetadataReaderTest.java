package com.drew.imaging.png;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Test;

import com.drew.lang.KeyValuePair;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.png.PngDirectory;

/**
 * @author Drew Noakes http://drewnoakes.com
 */
public class PngMetadataReaderTest
{
    @NotNull
    public static <T extends Directory> T processFile(@NotNull String filePath, @NotNull Class<T> directoryClass) throws IOException, PngProcessingException
    {
        T directory = processFile(filePath).getDirectory(directoryClass);
        assertNotNull(directory);
        return directory;
    }

    @NotNull
    private static Metadata processFile(@NotNull String filePath) throws PngProcessingException, IOException
    {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
            return PngMetadataReader.readMetadata(inputStream);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    @Test
    public void testGimpGreyscaleWithManyChunks() throws Exception
    {
        PngDirectory directory = processFile("Tests/Data/gimp-8x12-greyscale-alpha-time-background.png", PngDirectory.class);

        assertEquals(8, directory.getInt(PngDirectory.TAG_IMAGE_WIDTH));
        assertEquals(12, directory.getInt(PngDirectory.TAG_IMAGE_HEIGHT));
        assertEquals(8, directory.getInt(PngDirectory.TAG_BITS_PER_SAMPLE));
        assertEquals(4, directory.getInt(PngDirectory.TAG_COLOR_TYPE));
        assertEquals(0, directory.getInt(PngDirectory.TAG_COMPRESSION_TYPE));
        assertEquals(0, directory.getInt(PngDirectory.TAG_FILTER_METHOD));
        assertEquals(0, directory.getInt(PngDirectory.TAG_INTERLACE_METHOD));
        assertEquals(0.45455, directory.getDouble(PngDirectory.TAG_GAMMA), 0.00001);
        assertArrayEquals(new byte[]{0, 52}, directory.getByteArray(PngDirectory.TAG_BACKGROUND_COLOR));
        //noinspection ConstantConditions
        DateFormat fmt = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.US);
        fmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        assertEquals("Jan 1, 2013 4:08:30 AM", fmt.format(directory.getDate(PngDirectory.TAG_LAST_MODIFICATION_TIME)));
        @SuppressWarnings("unchecked")
        List<KeyValuePair> pairs = (List<KeyValuePair>)directory.getObject(PngDirectory.TAG_TEXTUAL_DATA);
        assertNotNull(pairs);
        assertEquals(1, pairs.size());
        assertEquals("Comment", pairs.get(0).getKey());
        assertEquals("Created with GIMP", pairs.get(0).getValue());
    }
}
