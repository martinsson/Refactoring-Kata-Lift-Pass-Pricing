<?php


class PricesTest extends TestCase
{
    /** @test */
    public function doesSomething()
    {
        $response = $this->call('GET', '/prices/?type=1jour');

        $response->assertOk();
        $response->assertJson([ 'cost' => 35 ]);
    }
}
