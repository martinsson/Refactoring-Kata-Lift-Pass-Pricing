export class PriceDao {
    constructor(private connection: any) {

    }

    public async findBasePrice(liftPassType) {
        const result = (await this.connection.query(
            'SELECT cost FROM `base_price` ' +
            'WHERE `type` = ? ',
            [liftPassType]))[0][0]
        return result
    }

    public async findAllHolidays() {
        const holidays = (await this.connection.query(
            'SELECT * FROM `holidays`'
        ))[0]
        return holidays
    }
}
