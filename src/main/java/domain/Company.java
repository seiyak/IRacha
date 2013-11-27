package domain;

public class Company {

	private String name;
	private String place;
	private String id;
	private String uri;

	public Company(String name, String place, String id, String uri) {
		this.name = name;
		this.place = place;
		this.id = id;
		this.uri = uri;
	}

	public Company(String name, String place, String uri) {
		this.name = name;
		this.place = place;
		this.id = "";
		this.uri = uri;
	}

	public Company(String name, String uri) {
		this.name = name;
		this.uri = uri;
		this.id = "";
		this.place = "";
	}

	public Company() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Company) {
			Company comp = (Company) obj;
			return name.equals(comp.getName()) && place.equals(comp.getPlace())
					&& id.equals(comp.getId()) && uri.equals(comp.getUri());
		}

		return false;
	}

	@Override
	public String toString() {
		return "Company:{name=" + name + " place=" + place + " id=" + id
				+ " uri=" + uri + "}";
	}
}
