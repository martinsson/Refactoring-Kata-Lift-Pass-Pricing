export class Cost {
    constructor(private rawCost: { cost: number }) {

    }

    public reduceByPercentage(percentOff: number) {
        return new Cost({cost: Math.ceil(this.rawCost.cost * (1 - percentOff / 100))})
    }

    getRaw() {
        return this.rawCost
    }

    static free() {
        return {cost: 0}
    }
}
