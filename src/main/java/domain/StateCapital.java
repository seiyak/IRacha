package domain;

public class StateCapital {

	private String state;
	private String capital;
	private double longitude;
	private double latitude;

	public StateCapital(String state, String capital, double longitude,
			double latitude) {
		this.state = state;
		this.capital = capital;
		this.longitude = longitude;
		this.latitude = latitude;
	}

	public StateCapital() {
		this.state = "";
		this.capital = "";
		this.longitude = 0;
		this.latitude = 0;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCapital() {
		return capital;
	}

	public void setCapital(String capital) {
		this.capital = capital;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof StateCapital) {
			StateCapital sc = (StateCapital) obj;
			return state.equals(sc.getState())
					&& capital.equals(sc.getCapital())
					&& longitude == sc.getLongitude()
					&& latitude == sc.getLatitude();
		}

		return false;
	}
}
