<?php

use Symfony\Bundle\FrameworkBundle\Test\WebTestCase;

class PricesControllerTest extends WebTestCase
{
    public function testGetPrices(): void
    {
        $client = static::createClient();

        $client->request('GET', '/prices?type=1jour');
        $this->assertEquals(200, $client->getResponse()->getStatusCode());
        $this->assertEquals(35, json_decode($client->getResponse()->getContent(), true)['cost']);
    }
}
