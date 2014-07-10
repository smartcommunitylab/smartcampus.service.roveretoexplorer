/*******************************************************************************
 * Copyright 2012-2014 Trento RISE
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 ******************************************************************************/
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
			if (to < from) {
				to = from;
			}
			double lat = 0;
			double lon = 0;
			String indirizzo = "";
			Object gpsObj = ((Map) part.get("gps")).get("value");
			if ((gpsObj instanceof String)) {
				String gps = (String) gpsObj;
				String loc[] = gps.split("\\|");
				
				indirizzo = loc[3].replace("#", "");
				
				try {
					lat = Double.parseDouble(loc[1].replace("#", ""));
				} catch (NumberFormatException e) {
				}
				try {
					lon = Double.parseDouble(loc[2].replace("#", ""));
				} catch (NumberFormatException e) {
				}			
			}
				
			Object tipoEvento = ((Map)part.get("tipo_evento")).get("value");
			List<String> tipi = new ArrayList<String>();
			if (tipoEvento instanceof Map) {
				tipi.add((String)((Map)tipoEvento).get("objectName"));
			} else if (tipoEvento instanceof List) {
				for (Object te: (List)tipoEvento) {
					tipi.add((String)(((Map)te)).get("objectName"));
				}
			}
			
			Object fonteObj = ((Map) part.get("fonte")).get("value");
			String fonte = fonteObj instanceof String ? (String) fonteObj : "";
			
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
			builder.setWebsiteUrl(url);
			builder.setFonte(fonte);
			builder.addAllTipo(tipi);
			
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
