package domain;

public class Average {

	private double commercial;
	private double residential;
	private double industrial;
	private String unit;

	public Average(double commercial, double residential, double industrial) {
		this.commercial = commercial;
		this.residential = residential;
		this.industrial = industrial;
		this.unit = "kWh";
	}

	public Average(double commercial, double residential, double industrial,
			String unit) {
		this.commercial = commercial;
		this.residential = residential;
		this.industrial = industrial;
		this.unit = unit;
	}

	public Average() {

	}

	public double getCommercial() {
		return commercial;
	}

	public void setCommercial(double commercial) {
		this.commercial = commercial;
	}

	public double getResidential() {
		return residential;
	}

	public void setResidential(double residential) {
		this.residential = residential;
	}

	public double getIndustrial() {
		return industrial;
	}

	public void setIndustrial(double industrial) {
		this.industrial = industrial;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof Average) {
			Average ave = (Average) obj;
			return commercial == ave.getCommercial()
					&& residential == ave.getResidential()
					&& industrial == ave.getIndustrial();
		}

		return false;
	}

	@Override
	public String toString() {
		return "Average={commercial=$" + commercial + " residential=$"
				+ residential + " industrial=$" + industrial + " unit=" + unit
				+ "}";
	}
}
