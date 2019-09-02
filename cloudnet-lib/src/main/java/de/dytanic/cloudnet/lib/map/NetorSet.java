package de.dytanic.cloudnet.lib.map;

final class NetorSet<VF, VS> {

    private VF valueF;
    private VS valueS;

    public NetorSet(VF valueF, VS valueS) {
        this.valueF = valueF;
        this.valueS = valueS;
    }

    public VF getFirstValue() {
        return valueF;
    }

    public VS getSecondValue() {
        return valueS;
    }

    public void updateFirst(VF value) {
        this.valueF = value;
    }

    public void updateSecond(VS value) {
        this.valueS = value;
    }

}
