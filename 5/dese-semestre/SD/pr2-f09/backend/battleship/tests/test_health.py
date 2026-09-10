from django.test import TestCase

class HealthTestCase(TestCase):
    def test_get(self):
        response = self.client.get(
            "/ht/"
        )
        self.assertEqual(response.status_code, 200)