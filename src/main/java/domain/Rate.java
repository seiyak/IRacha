package domain;

public class Rate {
	private String sector;
	private String unit;
	private String startDate;
	private double previousMonthly;
	private double currentMonthly;
	private double diff;
	private Company company;
	private Average average;

	public Rate() {
		this.sector = "Residential";
		this.unit = "kWh";
		this.startDate = "";
		this.currentMonthly = Double.MAX_VALUE;
		this.previousMonthly = Double.MAX_VALUE;
		this.diff = 0;
	}

	public String getSector() {
		return sector;
	}

	public void setSector(String sector) {
		this.sector = sector;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public double getPreviousMonthly() {
		return previousMonthly;
	}

	public void setPreviousMonthly(double previousMonthly) {
		this.previousMonthly = previousMonthly;
	}

	public double getCurrentMonthly() {
		return currentMonthly;
	}

	public void setCurrentMonthly(double currentMonthly) {
		double tmp = this.currentMonthly;
		this.currentMonthly = currentMonthly;
		diff = this.currentMonthly - tmp;
		this.previousMonthly = tmp;
	}

	public double getDiff() {
		return diff;
	}

	public void setDiff(double diff) {
		this.diff = diff;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Average getAverage() {
		return average;
	}

	public void setAverage(Average average) {
		this.average = average;
	}

	public Rate(String sector, String unit) {
		this.sector = sector;
		this.unit = unit;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Rate) {
			Rate rate = (Rate) obj;
			return sector.equals(rate.getSector())
					&& unit.equals(rate.getUnit()) && diff == rate.getDiff()
					&& company.equals(rate.getCompany())
					&& average.equals(rate.getAverage());
		}

		return false;
	}

	@Override
	public String toString() {
		return "Rate:{sector=" + sector + " unit=" + unit + " currentMonthly="
				+ currentMonthly + " previousMonthly=" + previousMonthly
				+ " diff=" + diff + " company=" + company + " average="
				+ average + "}";
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 17 + (int) currentMonthly;
		hash = hash * 31 + company.getName().hashCode();
		hash = hash * 13 + (average == null ? 0 : average.hashCode());
		return hash;
	}
}
