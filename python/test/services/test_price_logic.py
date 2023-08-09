from python.src.db import create_lift_pass_db_connection
from python.src.services.price_dao import PriceDao
from python.src.services.price_logic import PriceLogic
from python.src.setting import connection_options
import pytest


class TestPriceLogic:

    def test_calculate_price_with_age_inferior_to_6_return_0(self):
        """Ensure age 6 return price to 0"""
        connection = create_lift_pass_db_connection(connection_options)
        response = PriceLogic(PriceDao(connection)).calculate_price("1jour", 5)
        assert response == {'cost': 0}

    @pytest.mark.parametrize("age, expected_price", [(4, 0), (70, 27)])
    def test_calculate_price_with_specific_ages_get_reduction(self, age,
                                                              expected_price):
        """Ensure age between 6 and 15 and superior to 65 get reduction """
        connection = create_lift_pass_db_connection(connection_options)
        response = PriceLogic(PriceDao(connection)).calculate_price("1jour", age)
        assert response == {'cost': expected_price}

    @pytest.mark.parametrize("age, expected_price", [(7, 19), (65, 8)])
    def test_calculate_price_with_type_night_return_price(self, age, expected_price):
        """Ensure we retrieve different results with type night"""
        connection = create_lift_pass_db_connection(connection_options)
        response = PriceLogic(PriceDao(connection)).calculate_price("night", age)
        assert response == {'cost': expected_price}
