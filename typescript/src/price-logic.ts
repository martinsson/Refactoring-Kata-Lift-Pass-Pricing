import {PriceDao} from "./price-dao"
import {Cost} from "./cost"

export class PriceLogic {
    private dao: PriceDao

    constructor(priceDao: PriceDao) {
        this.dao = priceDao

    }

    async calculateCostFor(liftPassType, skiingDate, age = 25): Promise<Cost> {
        const rawCost = await this.dao.findBasePrice(liftPassType)

        let cost
        if (liftPassType === 'night') {
            cost = this.nightPass(age, rawCost)
        } else {
            let reduction = await this.calculateReduction(skiingDate)
            cost = this.dayPass(age, rawCost, reduction)
        }
        return cost
    }


    private dayPass(age, rawCost, reduction) {
        let result
        if (age < 6) {
            result = Cost.free()
        } else if (age < 15) {
            result = new Cost(rawCost).reduceByPercentage(30).getRaw()
        } else if (age > 74) {
            result = new Cost(rawCost).reduceByPercentage(60).getRaw()
        } else if (age > 64) {
            result = {cost: Math.ceil(rawCost.cost * .75 * reduction)}
        } else {
            result = {cost: Math.ceil(rawCost.cost * reduction)}
        }
        return result
    }

    private nightPass(age, rawCost) {
        let result
        if (age < 6) {
            result = Cost.free()
        } else if (age > 74) {
            result = new Cost(rawCost).reduceByPercentage(60).getRaw()
        } else {
            result = rawCost
        }
        return result
    }

    private async calculateReduction(skiingDate: string) {
        const holidays = await this.dao.findAllHolidays()
        let notAHoliday = this.notAHoliday(holidays, skiingDate)
        let reduction = 1
        if (notAHoliday && new Date(skiingDate).getDay() === 0) {
            reduction = 1 / 1.6
        }
        return reduction
    }

    private notAHoliday(holidays, skiingDate) {

        function extractDate(row) {
            return row.holiday.toISOString().split('T')[0]
        }

        return holidays
            .map(extractDate)
            .every(h => skiingDate && skiingDate !== h)
    }

}
