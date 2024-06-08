<?php


class PricesTest extends TestCase
{
    /** @test */
    public function doesSomething()
    {
        $response = $this->call('GET', '/prices/?type=1jour');

        $response->assertOk();
        $response->assertHeader('Content-Type', 'application/json')
                 ->assertJson([ 'cost' => 35 ]);
        );
    }
}
