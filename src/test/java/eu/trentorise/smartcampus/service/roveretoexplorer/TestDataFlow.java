package eu.trentorise.smartcampus.service.roveretoexplorer;

import it.sayservice.platform.client.InvocationException;
import it.sayservice.platform.client.ServiceBusClient;
import it.sayservice.platform.client.jms.JMSServiceBusClient;
import it.sayservice.platform.core.message.Core.ActionInvokeParameters;
import it.sayservice.platform.servicebus.test.DataFlowTestHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;

import junit.framework.TestCase;

import com.google.protobuf.Message;

import eu.trentorise.smartcampus.service.roveretoexplorer.data.message.Roveretoexplorer.EventoRovereto;
import eu.trentorise.smartcampus.service.roveretoexplorer.impl.GetEventiRoveretoDataFlow;

public class TestDataFlow extends TestCase {
	
	public void testRun() throws Exception {
		DataFlowTestHelper helper = new DataFlowTestHelper("test");
		Map<String, Object> parameters = new HashMap<String, Object>();

		Map<String, Object> out1 = helper.executeDataFlow("smartcampus.service.roveretoexplorer", "GetEventiRovereto", new GetEventiRoveretoDataFlow(), parameters);
		List<Message> data1 = (List<Message>)out1.get("data");
		for (Message msg: data1) {
			System.err.println(((EventoRovereto)msg).getDescrizione());
			System.err.println("-----------");
		}
		
	}
	
	public void testRemote() throws InvocationException {
		Map<String, Object> parameters = new HashMap<String, Object>();

		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
		ServiceBusClient client = new JMSServiceBusClient(connectionFactory);
		
		ActionInvokeParameters invokeService = client.invokeService("smartcampus.service.roveretoexplorer", "GetEventiRovereto", parameters);
		System.err.println(invokeService.getDataCount());
	}

}
