<?php


class PricesTest extends TestCase
{
    /** @test */
    public function daypassWithNoAge()
    {
        $response = $this->call('GET', '/prices/?type=1jour');

        $response->assertOk();
        $response->assertHeader('Content-Type', 'application/json')
                 ->assertJson([ 'cost' => 35 ]);
        );
    }

    /** @test */
    public function daypass()
    {
        $this->assertCostFor('1jour', 5, 0);
        $this->assertCostFor('1jour', 6, 25);
        $this->assertCostFor('1jour', 14, 25);
        $this->assertCostFor('1jour', 15, 35);
        $this->assertCostFor('1jour', 64, 35);
        $this->assertCostFor('1jour', 65, 27);
    }

    /**
     * @test
     * @dataProvider nightData
     */
    public function night($age, $expectedCost)
    {
        $this->assertCostFor('night', $age, $expectedCost);
    }

    public function nightData(): array
    {
        return [
            array(5, 0),
            array(6, 19),
            array(64, 19),
            array(65, 8),
        ];
    }

    private function assertCostFor(string $type, int $age, int $expectedCost): void
    {
        $this->get('/prices/?type=' . $type . '&age=' . $age . '');
        $this->seeJsonEquals(
            ['cost' => $expectedCost],
            $this->response->getContent()
        );
    }

}
