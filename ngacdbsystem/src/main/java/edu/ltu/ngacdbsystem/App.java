package edu.ltu.ngacdbsystem;

import edu.ltu.ngacdbsystem.influxImplementation.InfluxDatabase;
import edu.ltu.ngacdbsystem.obligationClasses.eventyml;
import edu.ltu.ngacdbsystem.obligationClasses.filtersyml;
import edu.ltu.ngacdbsystem.obligationClasses.obligationsyml;
import edu.ltu.ngacdbsystem.obligationClasses.operationsyml;
import edu.ltu.ngacdbsystem.obligationClasses.policyElementsyml;
import edu.ltu.ngacdbsystem.obligationClasses.responseyml;
import edu.ltu.ngacdbsystem.obligationClasses.rulesyml;
import edu.ltu.ngacdbsystem.obligationClasses.subjectyml;
import edu.ltu.ngacdbsystem.obligationClasses.targetyml;

import com.google.gson.JsonArray;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFormat.Feature;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.google.common.net.MediaType;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.opencsv.CSVWriter;
import com.sun.tools.javac.Main;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import edu.ltu.ngacdbsystem.IDatabase;
import edu.ltu.ngacdbsystem.DatabaseSelector;
import edu.ltu.ngacdbsystem.Database.DatabaseResponse;
import edu.ltu.ngacdbsystem.Database.DatabaseRequest;
import java.util.*;
import gov.nist.csd.pm.operations.Operations;
import gov.nist.csd.pm.pap.GraphAdmin;
import gov.nist.csd.pm.pap.ObligationsAdmin;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.ProhibitionsAdmin;
import gov.nist.csd.pm.operations.OperationSet;
import gov.nist.csd.pm.pdp.decider.Decider;
import gov.nist.csd.pm.pdp.decider.PReviewDecider;
import gov.nist.csd.pm.pdp.services.UserContext;
import gov.nist.csd.pm.pdp.audit.*;
import gov.nist.csd.pm.pdp.audit.model.Explain;
import gov.nist.csd.pm.epp.*;
import gov.nist.csd.pm.epp.events.AssignToEvent;
import gov.nist.csd.pm.epp.events.ObjectAccessEvent;
import gov.nist.csd.pm.exceptions.PMException;
import gov.nist.csd.pm.pip.graph.Graph;
import gov.nist.csd.pm.pip.graph.MemGraph;
import gov.nist.csd.pm.pip.graph.model.nodes.Node;
import gov.nist.csd.pm.pip.graph.model.nodes.NodeType;
import gov.nist.csd.pm.pip.obligations.MemObligations;
import gov.nist.csd.pm.pip.obligations.Obligations;
import gov.nist.csd.pm.pip.obligations.evr.EVRParser;
import gov.nist.csd.pm.pip.prohibitions.Prohibitions;
import gov.nist.csd.pm.pip.prohibitions.model.Prohibition;
import gov.nist.csd.pm.pip.prohibitions.MemProhibitions;
import gov.nist.csd.pm.pip.obligations.model.Obligation;
import gov.nist.csd.pm.pip.obligations.model.PolicyClass;
import gov.nist.csd.pm.pdp.PDP;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) throws PMException, IOException, InterruptedException {
		if (false) {
			evaluationSetup();
		} else {
			System.out.println("Fill(1) or query(2)?");
			Scanner scanner = new Scanner(System.in);
			String op = scanner.nextLine();
			if (op.equals("2")) {
				System.out.println("Hello World!");
				// Graphs*********************************************************************************************************
				PDP pdp = PDP.newPDP(
						new PAP(new GraphAdmin(new MemGraph()), new ProhibitionsAdmin(new MemProhibitions()),
								new ObligationsAdmin(new MemObligations())),
						new EPPOptions(), new OperationSet("read", "write", "execute"));

				Graph graph = pdp.getGraphService(new UserContext("super"));
				Node epc1 = graph.createPolicyClass("epc1", null);
				// User attributes
				Node uaEngineers = graph.createNode("uaEngineers", NodeType.UA, null, epc1.getName());
				Node uaSensors = graph.createNode("uaSensors", NodeType.UA, null, epc1.getName());
				Node uaMachEngineers = graph.createNode("uaMachEngineers", NodeType.UA, null, uaEngineers.getName());
				Node uaMainEngineers = graph.createNode("uaMainEngineers", NodeType.UA, null, uaEngineers.getName());
				Node uaT1 = graph.createNode("uaT1", NodeType.UA, null, uaSensors.getName());
				Node uaT2 = graph.createNode("uaT2", NodeType.UA, null, uaSensors.getName());
				Node uaE1 = graph.createNode("uaE1", NodeType.UA, null, uaSensors.getName());
				// Object attributes
				Node oaSensors = graph.createNode("oaSensors", NodeType.OA, null, epc1.getName());
				Node oaMachine1 = graph.createNode("oaMachine1", NodeType.OA, null, oaSensors.getName());
				Node oaTempSensors = graph.createNode("oaTempSensors", NodeType.OA, null, oaSensors.getName());
				Node oaT1 = graph.createNode("oaT1", NodeType.OA, null, oaMachine1.getName());
				Node oaT2 = graph.createNode("oaT2", NodeType.OA, null, oaTempSensors.getName());
				Node oaE1 = graph.createNode("oaE1", NodeType.OA, null, oaMachine1.getName());
				Node oaT1Type = graph.createNode("oaT1sensorType", NodeType.OA, null, oaT1.getName());
				Node oaT1Tag = graph.createNode("oaT1sensorTag", NodeType.OA, null, oaT1.getName());
				Node oaT2Type = graph.createNode("oaT2sensorType", NodeType.OA, null, oaT2.getName());
				Node oaT2Tag = graph.createNode("oaT2sensorTag", NodeType.OA, null, oaT2.getName());
				Node oaE1Type = graph.createNode("oaE1sensorType", NodeType.OA, null, oaE1.getName());
				Node oaE1Tag = graph.createNode("oaE1sensorTag", NodeType.OA, null, oaE1.getName());

				// Users
				Node uAlice = graph.createNode("uAlice", NodeType.U, null, uaMachEngineers.getName());
				Node uBob = graph.createNode("uBob", NodeType.U, null, uaMachEngineers.getName());
				Node uCharlie = graph.createNode("uCharlie", NodeType.U, null, uaMainEngineers.getName());
				Node uT1 = graph.createNode("uT1", NodeType.U, null, uaT1.getName());
				Node uT2 = graph.createNode("uT2", NodeType.U, null, uaT2.getName());
				Node uE1 = graph.createNode("uE1", NodeType.U, null, uaE1.getName());
				// Objects
				Node oT1 = graph.createNode("oT1", NodeType.O, null, oaT1.getName());
				Node oT2 = graph.createNode("oT2", NodeType.O, null, oaT2.getName());
				Node oE1 = graph.createNode("oE1", NodeType.O, null, oaE1.getName());
				Node oT1Type = graph.createNode("oT1sensorType", NodeType.O, null, oaT1Type.getName());
				Node oT2Type = graph.createNode("oT2sensorType", NodeType.O, null, oaT2Type.getName());
				Node oE1Type = graph.createNode("oE1sensorType", NodeType.O, null, oaE1Type.getName());
				Node oE1Tag = graph.createNode("oE1sensorTag", NodeType.O, null, oaE1Tag.getName());
				Node oT1Tag = graph.createNode("oT1sensorTag", NodeType.O, null, oaT1Tag.getName());
				Node oT2Tag = graph.createNode("oT2sensorTag", NodeType.O, null, oaT2Tag.getName());

				// Assigments
				graph.assign(oT1.getName(), oaTempSensors.getName());
				graph.assign(oaT1.getName(), oaTempSensors.getName());

				// Associations
				// Write Associations from sensor users to sensor objects
				graph.associate(uaT1.getName(), oaT1.getName(), new OperationSet(Operations.WRITE));
				graph.associate(uaT2.getName(), oaT2.getName(), new OperationSet(Operations.WRITE));
				graph.associate(uaE1.getName(), oaE1.getName(), new OperationSet(Operations.WRITE));
				graph.associate(uaMachEngineers.getName(), oaMachine1.getName(), new OperationSet(Operations.READ));
				graph.associate(uaMainEngineers.getName(), oaTempSensors.getName(),
						new OperationSet(Operations.READ, "F data < 100"));

				// Prohibitions
				Prohibitions prohibitions = new MemProhibitions();
				Prohibition prohibition = new Prohibition.Builder("test-prohibition", uaMainEngineers.getName(),
						new OperationSet(Operations.READ)).addContainer(oaT1Tag.getName(), false).build();
				prohibitions.add(prohibition);
				// End
				// Graphs*********************************************************************************************************

				// Database Query
				System.out.println("From which table?(T1, T2, E1)");
				String table = scanner.nextLine();

				System.out.println("Which columns? (sensorType, sensorTag)");
				String columns = scanner.nextLine();
				List<String> columnsList = new ArrayList<String>();
				if (columns.equals("*")) {
					columnsList = Arrays.asList("sensorType", "sensorTag");
				} else {
					columnsList = Arrays.asList(columns.split("\\s*,\\s*"));
				}

				System.out.println("How many points you need?");
				String n = scanner.nextLine();
				if (n == null) {
					System.out.println("No param given");
				} else {
					System.out.println("Printing: " + n + " points");
				}
				System.out.println("Who are you? (Alice, Bob, Charlie)");
				String user = scanner.nextLine();
				scanner.close();

				//////////////////////////////// *

				InputStream is = Main.class.getClassLoader().getResourceAsStream("epp/obligation_generated.yml");
				String yml = IOUtils.toString(is, StandardCharsets.UTF_8.name());
				UserContext superCtx = new UserContext("super");

				Obligation obligation = new EVRParser().parse(superCtx.getUser(), yml);
				pdp.getObligationsService(superCtx).add(obligation, true);
				List<String> filters = pdp.getEPP().processEventFilters(
						new ObjectAccessEvent(new UserContext("u" + user), "read", graph.getNode("o" + table)));
				//System.out.println("filters obli " + filters);
				////////////////////////////////

				// NGAC Decider stuff
				Decider decider = new PReviewDecider(graph, prohibitions, null);
				Set<String> permissions = decider.list("u" + user, "", "o" + table);
				System.out.println(permissions);
				boolean columnsAllow = true;
				for (String temp : columnsList) {
					System.out.println("Column: " + temp + " Permit: "
							+ decider.check("u" + user, "", "o" + table + temp, Operations.READ));
					columnsAllow = columnsAllow & decider.check("u" + user, "", "o" + table + temp, Operations.READ);
				}
				if (!permissions.isEmpty() && Arrays.stream(permissions.toArray()).anyMatch("read"::equals)
						&& columnsAllow) {
					// Database request
					DatabaseRequest request = new DatabaseRequest(n, table, "-influx", permissions);
					Gson gson = new Gson();
					String jsonText = gson.toJson(request);
					JsonObject queryJson = gson.fromJson(jsonText, JsonObject.class);
					IDatabase db = DatabaseSelector.getDatabaseInstance(queryJson.get("databaseType").getAsString());
					queryJson.addProperty("columnName", table);
					columns = columns + ", data";
					queryJson.addProperty("columns", columns);
					System.out.println("Query: " + queryJson.toString());
					System.out.println(db.requestQuery(queryJson));
					System.out.println(db.requestData(queryJson));

				} else {
					System.out.println("Permission denied for " + user + ".");
				}

			} else if (op.equals("1")) {
				// insert data to DB
				IDatabase db = DatabaseSelector.getDatabaseInstance("-influx");
				System.out.println("To which table?");
				String inTable = scanner.nextLine();
				if (db != null) {
					db.connect();
					System.out.println("Connected to DB");
				}
				for (int i = 0; i < 1000; i++) {
					db.insertData(JsonNize(gSensorData(inTable)));
				}
			}
		}
	}

	public static void evaluationSetup() throws PMException, IOException, InterruptedException {
		System.out.println("Hello World!");
		createObligations();
		// Graphs*********************************************************************************************************
		PDP pdp = PDP.newPDP(
				new PAP(new GraphAdmin(new MemGraph()), new ProhibitionsAdmin(new MemProhibitions()),
						new ObligationsAdmin(new MemObligations())),
				new EPPOptions(), new OperationSet("read", "write", "execute"));

		Graph graph = pdp.getGraphService(new UserContext("super"));
		Node epc1 = graph.createPolicyClass("epc1", null);

		// Create 100 UA nodes and 100 OA nodes (ua1, ua2, ..., ua100) (oa1, oa2, ...,
		// oa100)
		for (int i = 0; i <= 100; i++) {
			String userNodeName = "ua" + Integer.toString(i);
			String objectNodeName = "oa" + Integer.toString(i);
			graph.createNode(userNodeName, NodeType.UA, null, epc1.getName());
			graph.createNode(objectNodeName, NodeType.OA, null, epc1.getName());
		}

		// Create set of associations. 1 UA is assigned to 3 OA (1 -> 1,2,3; 2 -> 2,3,4;
		// 3 -> 3,4,5; ...)
		Random rand = new Random();
		for (int i = 0; i <= 100; i++) {
			for (int j = i; j < i+10; j++) {
				graph.associate("ua" + Integer.toString(i), "oa" + Integer.toString(j % 100),
						new OperationSet(Operations.READ, "F data > "+Integer.toString(rand.nextInt(500))));
				//graph.associate("ua" + Integer.toString(i), "oa" + Integer.toString(j % 100),
				//	new OperationSet(Operations.READ));
			}
		}

		Thread.sleep(5000);
		//////////////////////Obligation loading//////////////////////////
		InputStream is = Main.class.getClassLoader().getResourceAsStream("epp/obligation_generated.yml");
		String yml = IOUtils.toString(is, StandardCharsets.UTF_8.name());
		UserContext superCtx = new UserContext("super");

		Obligation obligation = new EVRParser().parse(superCtx.getUser(), yml);
		pdp.getObligationsService(superCtx).add(obligation, true);
		//////////////////////////////////////////////////////////////////
		//runEvaluation(2, graph, pdp);
		for (int r = 0; r <= 3; r++){
			//runEvaluation(0, graph, pdp);
			runEvaluation(2, graph, pdp);
			runEvaluation(1, graph, pdp);

		}
		
	}

	public static void createObligations() throws JsonGenerationException, JsonMappingException, IOException {
		List<rulesyml> rules = new ArrayList<rulesyml>();
		Random rand = new Random();
		for (int i = 0; i<=100; i++){ //create 100 rules
			for (int j = i; j<=i+10; j++){
				List<String> filters = new ArrayList<String>();
				filters.add("F data < " + Integer.toString(rand.nextInt(500))); //create random filter
				responseyml responseyml = new responseyml(filters);
				policyElementsyml policyElementsyml = new policyElementsyml("oa" + Integer.toString(j%100), "OA");
				List<policyElementsyml> policyElementsymls = new ArrayList<policyElementsyml>();
				policyElementsymls.add(policyElementsyml);
				targetyml targetyml = new targetyml(policyElementsymls);
				List<String> opsls = new ArrayList<String>();
				opsls.add("read");
				subjectyml subjectyml = new subjectyml("ua"+Integer.toString(i));
				eventyml eventyml = new eventyml(subjectyml, opsls, targetyml);
				rulesyml rulesyml = new rulesyml("u"+Integer.toString(i) + " to o" + Integer.toString(j%100), eventyml, responseyml);
				rules.add(rulesyml);
			}
		}
		obligationsyml obligation = new obligationsyml("generated obligation", rules);
		ObjectMapper om = new ObjectMapper(new YAMLFactory().enable(YAMLGenerator.Feature.MINIMIZE_QUOTES));
		om.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		om.writeValue(new File("ngacdbsystem/src/main/resources/epp/obligation_generated.yml"), obligation);
	}

	private static void runEvaluation(int approach, Graph graph, PDP pdp) throws PMException, IOException {
		File file = new File("ngacdbsystem/src/main/evaluation/results"+Integer.toString(approach)+".csv");
		FileWriter outputFile = new FileWriter(file);
		CSVWriter writer = new CSVWriter(outputFile);
		String[] header = {"node i", "node j", "query", "t0", "t1", "t2"};
		int rowTrack = 0;
		writer.writeNext(header);
		for (int repe = 0; repe<=3; repe++){
			for (int i = 0; i<=100; i++){
				for (int j = i; j<=i+9; j++){
					String user = "ua"+Integer.toString(i);
					String obj = "oa"+Integer.toString(j%100);
					Decider decider = new PReviewDecider(graph, null);
					Set<String> filterSet = Collections.<String>emptySet();
					String queryStr = "";
					long t0 = System.nanoTime();
					boolean allowed = decider.check(user, "", obj, Operations.READ);
					long t1 = System.nanoTime();
					if (allowed){
						//System.out.println("allowed");
						if (approach == 1){
							//assignment metadata
							filterSet = decider.list(user, "", obj);
						}else if(approach == 2){
							//obligations
							List<String> filters = pdp.getEPP().processEventFilters(
								new ObjectAccessEvent(new UserContext(user), "read", graph.getNode(obj)));
							filterSet = new HashSet<>(filters);
						}else{
							//base
							//nanais
						}
						//now that we have the filters:
						DatabaseRequest request = new DatabaseRequest("100", obj, "-influx", filterSet);
						Gson gson = new Gson();
						String jsonText = gson.toJson(request);
						JsonObject queryJson = gson.fromJson(jsonText, JsonObject.class);
						IDatabase db = DatabaseSelector.getDatabaseInstance(queryJson.get("databaseType").getAsString());
						queryJson.addProperty("columnName", obj);
						String columns = "*";
						queryJson.addProperty("columns", columns);
						queryStr = db.requestQuery(queryJson);
					}
					long t2 = System.nanoTime();
					String[] row = {"ua" + Integer.toString(i), "oa"+Integer.toString(j%1000), queryStr, Long.toString(t0), Long.toString(t1), Long.toString(t2)};
					writer.writeNext(row);
				}
			}
		}
		writer.close();
	}

	private static sensorData gSensorData(String table){
		int leftLimit = 97;
		int rightLimit = 122;
		int strlenght = 5;
		Random rand = new Random();
		System.out.println("servicio");
		String generatedString = rand.ints(leftLimit, rightLimit + 1)
			.limit(strlenght)
			.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
			.toString();
		sensorData rSensorData = new sensorData(rand.nextInt(500), table, generatedString, System.currentTimeMillis());
		return rSensorData;
	}
	private static JsonObject JsonNize(final sensorData object){
		JsonObject json = new JsonObject();
		json.addProperty("sensorType",object.getSensorType());
		json.addProperty("timeStamp", object.getTimeStamp());
		json.addProperty("data",object.getData());
		json.addProperty("sensorTag", object.getTag());
		return json;
	}

}
