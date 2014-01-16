package eu.trentorise.smartcampus.service.roveretoexplorer.scripts;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import com.google.protobuf.Message;

import eu.trentorise.smartcampus.service.roveretoexplorer.data.message.Roveretoexplorer.Address;
import eu.trentorise.smartcampus.service.roveretoexplorer.data.message.Roveretoexplorer.EventoRovereto;

public class RoveretoExplorerScript {

	public List<Message> extractData(String json) throws Exception {
		List<Message> result = new ArrayList<Message>();

		ObjectMapper mapper = new ObjectMapper();
		List list = mapper.readValue(json, List.class);
		for (Object o : list) {
			try {
			Map map = mapper.convertValue(o, Map.class);
			String link = (String) map.get("link");
			Map newJson = mapper.readValue(new URL(link), Map.class);
			Map part = (Map) newJson.get("fields");

			String titolo = (String) ((Map) part.get("titolo")).get("value");
			String image = getValue(((Map) part.get("image")).get("value"));
			if (image instanceof String) {
				// System.out.println(image);
			}
			String whereWhen = (String) ((Map) part.get("periodo_svolgimento")).get("value");
			String shortDescription = (String) ((Map) part.get("abstract")).get("value");
			String info = (String) ((Map) part.get("informazioni")).get("value");
			long from = 1000 * Long.parseLong((String) ((Map) part.get("from_time")).get("value"));
			long to = 1000 * Long.parseLong((String) ((Map) part.get("to_time")).get("value"));
			String gps = (String) ((Map) part.get("gps")).get("value");
			String tipo = (String)((Map) ((Map) part.get("tipo_evento")).get("value")).get("objectName");
			String fonte = (String) ((Map) part.get("fonte")).get("value");
			
			String loc[] = gps.split("\\|");
			
			String indirizzo = loc[3].replace("#", "");
			double lat = 0;
			double lon = 0;
			
			try {
				lat = Double.parseDouble(loc[1].replace("#", ""));
			} catch (NumberFormatException e) {
			}
			try {
				lon = Double.parseDouble(loc[2].replace("#", ""));
			} catch (NumberFormatException e) {
			}			
			
			part = (Map) newJson.get("metadata");
			
			String id = "" + part.get("nodeId");
			String url = (String) part.get("fullUrl");
			
			EventoRovereto.Builder builder = EventoRovereto.newBuilder();
			builder.setId(id);
			if (from != 0) {
				builder.setFromTime(from);
			}
			if (to != 0) {
				builder.setToTime(to);
			}
			if (image != null) {
				builder.setImage(image);
			}
			
			Address.Builder ab = Address.newBuilder();
			ab.setStreet(indirizzo);
			ab.setPlace("");
			ab.setTown("");
			
			builder.setIndirizzo(ab.build());
			
			if (lat != 0) {
				builder.setLat(lat);
			}
			if (lon != 0) {
				builder.setLon(lon);
			}
			builder.setWhenWhere(whereWhen);
//			builder.setSommario(shortDescription);
//			builder.setInformazioni(info);
			String descr = shortDescription + "\n" + info;
			builder.setDescrizione(descr);
			builder.setTitolo(titolo);
			builder.setUrl(url);
			builder.setFonte(fonte);
			builder.setTipo(tipo);
			
//			Contacts.Builder cb = Contacts.newBuilder();
//			cb.setEmail("");
//			cb.setFacebook("");
//			cb.setFax("");
//			cb.setPhone("");
//			cb.setTwitter("");
			
//			builder.setContacts(cb.build());
			
			result.add(builder.build());
			
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return result;
	}

	private String getValue(Object o) {
		if (o instanceof String) {
			return (String) o;
		}
		return null;
	}

}
