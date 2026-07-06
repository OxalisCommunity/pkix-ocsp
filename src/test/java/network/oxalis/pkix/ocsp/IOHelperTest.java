package network.oxalis.pkix.ocsp;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author erlend
 */
public class IOHelperTest {

    // check byte array manipulation
    @Test
    public void fileInputStreamToByteArrayTest() throws IOException {
        // Arrange
        var certBytes = Files.readAllBytes(Paths.get("src/test/resources/selfsigned.cer"));

        // Act
        byte[] fisBytes;
        try (var fis = new FileInputStream("src/test/resources/selfsigned.cer")) {
            fisBytes = IOHelper.toByteArray(fis);
        }

        // Assert
        Assert.assertEquals(fisBytes, certBytes);
    }

    @Test
    public void emptyStreamReturnsEmptyArray() throws IOException {
        var result = IOHelper.toByteArray(new ByteArrayInputStream(new byte[0]));

        Assert.assertEquals(result.length, 0);
    }
}
