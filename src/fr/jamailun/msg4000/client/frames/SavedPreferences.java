package fr.jamailun.msg4000.client.frames;

import fr.jamailun.msg4000.common.StaticConfiguration;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SavedPreferences {
	private String uri;

	public final static String KEY_ADDRESS = "address";
	public final static String KEY_USERNAME = "username";

	private Map<String, String> prefs = new HashMap<>();

	public SavedPreferences() {
		String tempDir = System.getProperty("java.io.tmpdir");
		uri = tempDir + "messaging4000.prefs";
		File file = new File(uri);
		if( ! file.exists()) {
			try {
				boolean success = file.createNewFile();
				if(!success)
					System.err.println("Could not create the file ["+uri+"].");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		//System.out.println("temp : " +uri);
		loadPrefs();
	}

	private void loadPrefs() {
		try(BufferedReader reader = new BufferedReader(new FileReader(uri))) {
			String line;
			while((line = reader.readLine()) != null) {
				if(line.startsWith("#"))
					continue;
				String[] parts = line.split("=");
				if(parts.length < 2)
					continue;
				prefs.put(parts[0], parts[1]);
				//System.out.println(parts[0] + " -> " + parts[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(!(prefs.containsKey(KEY_ADDRESS) && prefs.containsKey(KEY_USERNAME))) {
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(uri))) {
				if( ! prefs.containsKey(KEY_USERNAME)) {
					String username = "michel_"+ new Random().nextInt(10000);
					writer.write("username=" + username );
					writer.newLine();
					writer.flush();
					prefs.put(KEY_USERNAME, username);
				}
				if( ! prefs.containsKey(KEY_ADDRESS)) {
					String address = StaticConfiguration.address+":"+StaticConfiguration.port;
					writer.write("address=" + address);
					writer.newLine();
					writer.flush();
					prefs.put(KEY_ADDRESS, address);
				}
			} catch (IOException e) {
				e.printStackTrace();
				prefs.put(KEY_ADDRESS, "");
				prefs.put(KEY_USERNAME, "");
			}
		}
	}

	public String getPreference(String key) {
		return prefs.get(key);
	}

	public void changePreference(String key, String value) {
		if(!prefs.containsKey(key))
			throw new IllegalArgumentException("Unknown key value.");
		if(prefs.get(key).equals(value))
			return;
		prefs.replace(key, value);
		try {
			new FileWriter(uri, false).close();

			BufferedWriter writer = new BufferedWriter(new FileWriter(uri));
			prefs.forEach((k,v) -> {
				try {
					writer.write(k+"="+v);
					writer.newLine();
					writer.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}