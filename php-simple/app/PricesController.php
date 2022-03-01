<?php


namespace App;

use Illuminate\Http\Request;
use Laravel\Lumen\Routing\Controller;

class PricesController extends Controller {
    public function get(Request $request) {


        $basePrice = app('db')->select(
            "SELECT cost FROM base_price " .
            "WHERE type = ?", [$request->input("type")])[0]->cost;

        if ($request->input("age") != null && $request->input("age") < 6) {
            return "{ \"cost\": 0}";
        } else {

            if ($request->input("type") != "night") {

                if ($request->input("age") != null && $request->input("age") < 15) {
                    $cost = $basePrice * (1 - 30 / 100.0);
                    return "{ \"cost\": " . (int)ceil($cost) . "}";
                } else {
                    if ($request->input("age") == null) {
                        return "{ \"cost\": " . (int)ceil($basePrice) . "}";
                    } else {
                        if ($request->input("age") > 64) {
                            $cost = $basePrice * (1 - 25 / 100.0);
                            return "{ \"cost\": " . (int)ceil($cost) . "}";
                        } else {
                            return "{ \"cost\": " . (int)ceil($basePrice) . "}";
                        }
                    }
                }
            } else {
                if ($request->input("age") != null && $request->input("age") >= 6) {
                    if ($request->input("age") > 64) {
                        return "{ \"cost\": " . (int)ceil($basePrice * .4) . "}";
                    } else {
                        return "{ \"cost\": " . $basePrice . "}";
                    }
                } else {
                    return "{ \"cost\": 0}";
                }
            }
        }

    }
}
