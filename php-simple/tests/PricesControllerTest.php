<?php


class PricesTest extends TestCase
{
    /** @test */
    public function doesSomething()
    {
        $this->get('/prices/?type=1jour');

        $this->seeJsonEquals(
            ['cost'=>35],
            $this->response->getContent()
        );
    }
}
