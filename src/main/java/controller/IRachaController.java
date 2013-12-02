package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import patricia.trie.PatriciaTrie;
import domain.Average;
import domain.Company;
import domain.Rate;
import domain.StateCapital;

@Controller
public class IRachaController {

	private static final String ALL_COMPANIES_REQUEST = "http://en.openei.org/services/rest/utility_companies?version=2&format=json_plain";
	private static final String MONTHLY_REQUEST_BASE = "http://en.openei.org/services/rest/utility_rates?version=latest&limit=5&detail=full&format=json_plain&ratesforutility=";
	private static final String ALL_STATE_CAPITALS = "http://www.xfront.com/us_states/";
	private static final String ITEMS = "items";
	private static final String LABEL = "label";
	private static final String URI = "uri";
	private static final String RESIDENTIAL_SEARCH = "li:contains(Residential)";
	private static final String COMMERCIAL_SEARCH = "li:contains(Commercial)";
	private static final String INDUSTRIAL_SEARCH = "li:contains(Industrial)";
	private static final String RESIDENTIAL_AVERAGE_PREFIX = "Residential: $";
	private static final String COMMERCIAL_AVERAGE_PREFIX = "Commercial: $";
	private static final String INDUSTRIAL_AVERAGE_PREFIX = "Industrial: $";
	private static final String UNIT = "/kWh";
	private final PatriciaTrie companyNamePat;
	private final PatriciaTrie statePat;
	private final HashMap<String, TreeSet<Rate>> stateMap;
	private final HashMap<String, Rate> companyNameMap;
	private final List<Rate> all;

	public IRachaController() {
		companyNamePat = new PatriciaTrie();
		statePat = new PatriciaTrie();
		stateMap = new HashMap<String, TreeSet<Rate>>();
		companyNameMap = new HashMap<String, Rate>();
		all = new ArrayList<Rate>();
		createRate(populateAsJSON(populateDatabase()));
	}

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home() {
		return "home";
	}

	private void createRate(JSONObject json) {

		JSONObject js = null;
		JSONArray ja = json.getJSONArray(ITEMS);
		// for (int i = 0; i < ja.length(); i++) {
		for (int i = 0; i < 50; i++) {
			js = ja.getJSONObject(i);
			Company comp = new Company(js.getString(LABEL), js.getString(URI));
			Average average = appendCompanyInfo(comp);
			Rate rate = appendRate(comp);
			rate.setCompany(comp);
			rate.setAverage(average);
			companyNamePat.insert(comp.getName());
			statePat.insert(comp.getPlace());
			addState(rate);
			companyNameMap.put(comp.getName(), rate);
			all.add(rate);

			System.out.println("json rate: " + i + " "
					+ new JSONObject(rate).toString());
		}

		Collections.sort(all, (new Comparator<Rate>() {

			@Override
			public int compare(Rate o1, Rate o2) {

				if (o1.getCurrentMonthly() < o2.getCurrentMonthly())
					return -1;
				else if (o1.getCurrentMonthly() > o2.getCurrentMonthly())
					return 1;

				return 0;
			}
		}));

		for (int i = 0; i < all.size(); i++) {
			System.out.println("rank i: " + i + " monthly charge: "
					+ all.get(i).getCurrentMonthly() + " name: "
					+ all.get(i).getCompany().getName() + " state: "
					+ all.get(i).getCompany().getPlace());
		}

		System.out.println("\n");
		Iterator<Rate> it = stateMap.get("Ohio").iterator();
		int rank = 1;
		while (it.hasNext()) {
			Rate r = it.next();
			System.out.println("Ohio: rank: " + rank + " "
					+ r.getCurrentMonthly() + " name: "
					+ r.getCompany().getName() + " state: "
					+ r.getCompany().getPlace());
			rank++;
		}

		rank = 1;

		System.out.println("\n");
		it = stateMap.get("Alaska").iterator();
		while (it.hasNext()) {
			Rate r = it.next();
			System.out.println("Alaska: rank: " + rank + " "
					+ r.getCurrentMonthly() + " name: "
					+ r.getCompany().getName() + " state: "
					+ r.getCompany().getPlace());
			rank++;
		}
	}

	private void addState(Rate rate) {

		if (stateMap.get(rate.getCompany().getPlace()) == null) {
			// first time to access
			stateMap.put(rate.getCompany().getPlace(), new TreeSet<Rate>());
		}

		stateMap.get(rate.getCompany().getPlace()).add(rate);
	}

	private Rate appendRate(Company comp) {

		Rate rate = new Rate();
		String tmp = "";

		tmp = readRates(comp.getName());
		JSONObject json = null;

		try {
			json = new JSONObject(tmp);
		} catch (JSONException e) {
			System.err.println("could not read rate for " + comp.getName()
					+ ". JSON has some issue.");
			return rate;
		}
		try {

			JSONObject js = null;
			JSONArray ja = json.getJSONArray(ITEMS);
			for (int i = 0; i < ja.length(); i++) {
				js = ja.getJSONObject(i);
				if (js.get("sector").equals("Residential")) {
					try {
						if (js.getString("startdate") != null) {
							rate.setStartDate(js.getString("startdate"));
						}
					} catch (JSONException ex) {
						System.err.println("could not find 'startdate' key");
						rate.setStartDate("-");
					}
					try {
						if (js.get("fixedmonthlycharge") != null) {
							rate.setCurrentMonthly(js
									.getDouble("fixedmonthlycharge"));
						}
					} catch (JSONException ex) {
						System.err
								.println("could not find 'fixedmonthlycharge' key");
						rate.setCurrentMonthly(Double.MAX_VALUE);
					}
					try {
						if (js.getString("demandrateunit") != null) {
							rate.setUnit(js.getString("demandrateunit"));
						}
					} catch (JSONException ex) {
						System.err
								.println("could not find 'demandrateunit' key");
						rate.setUnit(UNIT);
					}
				}
			}

		} catch (JSONException e) {
			System.err.println("could not read rate for " + comp.getName()
					+ ". JSON has some issue.");
			System.err.println("tmp: " + tmp);
		}

		return rate;
	}

	private String readRates(String companyName) {

		String ntmp = companyName;
		ntmp = ntmp.replaceAll(" ", "%20");
		URL rates = null;
		try {
			rates = new URL(MONTHLY_REQUEST_BASE + ntmp);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("rates url: " + rates);
		URLConnection conn = null;
		try {
			conn = rates.openConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedReader in = null;
		try {
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String inputLine, tmp = "";
		try {
			while ((inputLine = in.readLine()) != null) {
				tmp += inputLine;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return tmp;
	}

	private Average appendCompanyInfo(Company comp) {

		return parseAverage(parseCompanyInfo(comp));
	}

	private Document parseCompanyInfo(Company comp) {

		Document doc = null;
		try {
			doc = Jsoup.connect(comp.getUri()).timeout(10 * 1000).get();
			Elements els = doc.select("table.openei-infobox > tbody > tr > td");
			int len = els.size();
			for (int i = 0; i < len; i++) {
				if (i > 2) {
					break;
				}
				if (i == 0) {
					comp.setName(els.get(i).text());
				} else if (i == 1) {
					comp.setPlace(els.get(i).text());
				} else if (i == 2) {
					comp.setId(els.get(i).text());
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return doc;
	}

	private Average parseAverage(Document doc) {
		Average ave = new Average();
		ave.setResidential(prsAvarage(doc, RESIDENTIAL_SEARCH,
				RESIDENTIAL_AVERAGE_PREFIX));
		ave.setIndustrial(prsAvarage(doc, INDUSTRIAL_SEARCH,
				INDUSTRIAL_AVERAGE_PREFIX));
		ave.setCommercial(prsAvarage(doc, COMMERCIAL_SEARCH,
				COMMERCIAL_AVERAGE_PREFIX));
		ave.setUnit(UNIT);

		return ave;
	}

	private double prsAvarage(Document doc, String pattern, String averagePrefix) {

		Elements els = doc.select(pattern);
		int len = els.size();
		for (int i = 0; i < len; i++) {
			if (els.get(i).html().startsWith(averagePrefix)
					&& els.get(i).html().endsWith(UNIT)) {
				return Double.parseDouble(els
						.get(i)
						.html()
						.substring(averagePrefix.length(),
								els.get(i).html().indexOf(UNIT)));
			}
		}

		return Double.MIN_NORMAL;
	}

	private JSONObject populateAsJSON(String str) {

		JSONObject json = new JSONObject(str);
		return json;
	}

	private String populateDatabase() {
		String str = "";
		try {
			str = parseAllCompanies();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str;
	}

	private String parseAllCompanies() throws IOException {
		URL companies = new URL(ALL_COMPANIES_REQUEST);
		URLConnection conn = companies.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		String inputLine, tmp = "";
		while ((inputLine = in.readLine()) != null)
			tmp += inputLine;
		in.close();

		return tmp;
	}

	@RequestMapping(value = "/service/prefix", method = RequestMethod.GET)
	public void searchPrefix(@RequestHeader("prefix") String prefix,
			HttpServletResponse response) throws MalformedURLException {

		List<String> results = null;
		try {
			results = companyNamePat.searchPrefix(prefix);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("prefix from company name size: " + results.size());
		List<String> results2 = statePat.searchPrefix(prefix);
		System.out.println("prefix from state size: " + results2.size());

		Set<String> sets = new TreeSet<String>();
		for (int i = 0; i < results.size(); i++) {
			sets.add(results.get(i));
		}

		for (int i = 0; i < results2.size(); i++) {
			sets.add(results2.get(i));
		}

		Iterator<String> itr = sets.iterator();
		JSONObject json = new JSONObject();
		JSONArray ja = new JSONArray();
		int index = 0;
		while (itr.hasNext()) {
			// System.out.println("prefixed: " + itr.next());
			if (index > 10) {
				break;
			}
			Map<String, String> map = new LinkedHashMap<String, String>();
			map.put("prefix-" + index, itr.next());
			ja.put(map);
			index++;
		}
		json.put("data", ja);
		System.out.println("json for perfix: " + json.toString());
		writeResponseWithJSON(response, json.toString());
	}

	@RequestMapping(value = "/service/specific", method = RequestMethod.GET)
	public String searchSpecific(@RequestHeader("specific") String specific,
			HttpServletResponse response) throws MalformedURLException {

		Iterator<Rate> itr = null;
		TreeSet<Rate> rates = null;
		JSONObject json = new JSONObject();
		JSONArray ja = new JSONArray();
		Map<String, JSONObject> m = new HashMap<String, JSONObject>();

		try {
			if (statePat.search(specific)) {
				json.put("kind", "state");
				rates = stateMap.get(specific);

				itr = rates.iterator();
				int count = 0;
				while (itr.hasNext()) {
					if (count == 5) {
						break;
					}
					Rate r = itr.next();
					json.put("kind", "company");
					json.put("rank-state", -1);
					json.put("rank-us", -1);
					// m.put("company-0", companyNameMap.get(specific));

					r.setRankInState(getRankForCompanyInState(r));
					r.setRankInUS(getRankInUS(r.getCompany().getName()));
					Map<String, JSONObject> mm = new HashMap<String, JSONObject>();
					mm.put("company-" + count, new JSONObject(r));
					ja.put(mm);
					count++;
				}
			} else if (companyNamePat.search(specific)) {
				json.put("kind", "company");
				// json.put("rank-state",
				// getRankForCompanyInState(companyNameMap.get(specific)));
				// json.put("rank-us", getRankInUS(specific));
				// m.put("company-0", companyNameMap.get(specific));

				json.put("rank-state", -1);
				json.put("rank-us", -1);
				// m.put("company-0", companyNameMap.get(specific));

				companyNameMap.get(specific).setRankInState(
						getRankForCompanyInState(companyNameMap.get(specific)));
				companyNameMap.get(specific).setRankInUS(
						getRankInUS(companyNameMap.get(specific).getCompany()
								.getName()));
				m.put("company-0", new JSONObject(companyNameMap.get(specific)));
				ja.put(m);
			}
		} catch (NullPointerException ex) {
			System.err.println("found null pointer exception for specific: "
					+ specific);
			System.err.println("specific, " + specific
					+ " might be a company name.");

			json.put("kind", "company");
			json.put("rank-state", -1);
			json.put("rank-us", -1);

			companyNameMap.get(specific).setRankInState(
					getRankForCompanyInState(companyNameMap.get(specific)));
			companyNameMap.get(specific).setRankInUS(
					getRankInUS(companyNameMap.get(specific).getCompany()
							.getName()));
			m.put("company-0", new JSONObject(companyNameMap.get(specific)));
			ja.put(m);
		}

		json.put("data", ja);
		System.out.println("json for searchSpecific(): " + json.toString());
		writeResponseWithJSON(response, json.toString());
		return "home";
	}

	private int getRankForCompanyInState(Rate rate) {

		System.out.println("state map for: " + rate.getCompany().getPlace()
				+ " monthly charge: " + rate.getCurrentMonthly());
		if (rate == null || rate.getCurrentMonthly() == Double.MAX_VALUE) {
			return -1;
		}

		Iterator<Rate> itr = stateMap.get(rate.getCompany().getPlace())
				.iterator();
		if (itr != null) {
			int rank = 1;
			while (itr.hasNext()) {
				Rate r = itr.next();
				System.out.println("rank: " + rank + " r name: "
						+ r.getCompany().getName() + " rate name: "
						+ rate.getCompany().getName());
				if (r.getCompany().getName()
						.equals(rate.getCompany().getName())) {
					System.out.println("found state rank: " + rank + " for: "
							+ rate.getCompany().getName());
					return rank;
				}

				rank++;
			}
		}

		return Integer.MAX_VALUE;
	}

	private int getRankInUS(String companyName) {

		if (companyName == null || companyName.equals("")) {
			return -1;
		}

		System.out.println("companyName fpr rank in us: " + companyName);
		Iterator<Rate> itr = all.iterator();
		if (itr != null) {
			int rank = 1;
			Rate r = null;
			while (itr.hasNext()) {
				r = itr.next();
				System.out.println("rank in us, " + rank + " r name: "
						+ r.getCompany().getName() + " companyName: "
						+ companyName);
				if (r.getCompany().getName().equals(companyName)) {
					if (r.getCurrentMonthly() > 100) {
						return -1;
					}

					System.out.println("found us rank: " + rank + " for: "
							+ r.getCompany().getName());
					return rank;
				}

				rank++;
			}
		}

		return Integer.MAX_VALUE;
	}

	@RequestMapping(value = "/service/multiple-states", method = RequestMethod.GET)
	public String searchMultipleState(
			@RequestParam("multiple-states") String states,
			HttpServletResponse response) throws MalformedURLException {

		System.out.println("states: " + states);

		String[] sts = states.split(",");
		Iterator<Rate> itr = null;
		JSONObject json = new JSONObject();
		JSONArray ja = new JSONArray();
		List<Rate> rates = new ArrayList<Rate>();
		for (String st : sts) {
			System.out.println("each parametr: " + st);

			if (stateMap.get(st) == null) {
				continue;
			}
			itr = stateMap.get(st).iterator();

			System.out.println("state map size: " + stateMap.get(st).size());
			int count = 0;
			while (itr.hasNext()) {
				if (count == 3) {
					break;
				}
				/*
				 * Rate r = itr.next(); System.out.println("multiple r: " + r);
				 * json.put("kind", "company"); json.put("rank-state", -1);
				 * json.put("rank-us", -1);
				 * 
				 * r.setRankInState(getRankForCompanyInState(r));
				 * r.setRankInUS(getRankInUS(r.getCompany().getName())); //
				 * m.put("company-0", companyNameMap.get(specific)); Map<String,
				 * JSONObject> m = new HashMap<String, JSONObject>();
				 * 
				 * m.put("company-" + index, new JSONObject(r)); ja.put(m);
				 */
				rates.add(itr.next());
				count++;
			}
		}

		Collections.sort(rates, new Comparator<Rate>() {

			@Override
			public int compare(Rate o1, Rate o2) {
				// TODO Auto-generated method stub
				return (int) (o1.getCurrentMonthly() - o2.getCurrentMonthly());
			}
		});

		for (int i = 0; i < rates.size(); i++) {
			Rate r = rates.get(i);
			json.put("kind", "company");
			json.put("rank-state", -1);
			json.put("rank-us", -1);

			r.setRankInState(getRankForCompanyInState(r));
			r.setRankInUS(getRankInUS(r.getCompany().getName()));
			Map<String, JSONObject> m = new HashMap<String, JSONObject>();
			m.put("company-" + i, new JSONObject(r));
			ja.put(m);

		}

		json.put("data", ja);
		writeResponseWithJSON(response, json.toString());

		return "home";
	}

	@RequestMapping(value = "/service/capitals", method = RequestMethod.GET)
	public String getAllStateCapitals(HttpServletResponse response)
			throws MalformedURLException {

		List<StateCapital> capitals = parseAllStateCapitals();
		JSONObject json = new JSONObject();
		JSONArray js = new JSONArray();
		for (int i = 0; i < capitals.size(); i++) {
			Map<String, JSONObject> map = new HashMap<String, JSONObject>();
			map.put("capital-" + i, new JSONObject(capitals.get(i)));
			js.put(map);
		}

		json.put("data", js);
		writeResponseWithJSON(response, json.toString());
		return "home";
	}

	private List<StateCapital> parseAllStateCapitals() {

		List<StateCapital> capitals = new ArrayList<StateCapital>();

		Document doc = null;
		try {
			doc = Jsoup.connect(ALL_STATE_CAPITALS).timeout(10 * 1000).get();
			Elements els = doc.select("#container > ol > li > p");
			int len = els.size();
			int count = 0;
			int idx = 0;
			String state = "";
			String capital = "";
			double lng = 0, lat = 0;
			for (int i = 0; i < len; i++) {
				if (count == 4) {
					System.out.println("\n");
					count = 0;
					capitals.add(new StateCapital(state, capital, lng, lat));
					state = capital = "";
					lng = lat = 0;
				}

				if (count == 0) {
					idx = els.get(i).text().indexOf(":") + 2;
					state = els.get(i).text().substring(idx);
				} else if (count == 1) {
					idx = els.get(i).text().indexOf(":") + 2;
					capital = els.get(i).text().substring(idx);
				} else if (count == 2) {
					idx = els.get(i).text().indexOf(":") + 2;
					lat = Double.parseDouble(els.get(i).text().substring(idx));
				} else if (count == 3) {
					idx = els.get(i).text().indexOf(":") + 2;
					lng = Double.parseDouble(els.get(i).text().substring(idx));
				}

				count++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return capitals;
	}

	/**
	 * Writes prediction results as JSON.
	 * 
	 * @param response
	 *            Response object for the client holding the prediction results.
	 * @param json
	 *            JSON string representing the prediction results.
	 */
	protected void writeResponseWithJSON(HttpServletResponse response,
			String json) {

		if (response == null) {
			return;
		}

		HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper(
				response);
		wrapper.setContentType("application/json");
		wrapper.setHeader("Content-Length", "" + json.getBytes().length);
		try {
			response.getWriter().print(json);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
