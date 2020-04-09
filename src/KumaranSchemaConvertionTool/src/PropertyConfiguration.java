import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyConfiguration {
	static Properties props = new Properties();
	public Properties propertiyReader() throws IOException {
		InputStream conProp = new FileInputStream("src/connectivity.properties");
		props.load(conProp);
		conProp.close();
		return props;
	}
}
