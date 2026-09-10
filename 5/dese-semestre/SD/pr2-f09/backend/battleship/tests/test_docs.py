from django.test import TestCase

class DocumentationTestCase(TestCase):
    def test_get(self):
        response = self.client.get(
            "/docs/"
        )
        self.assertEqual(response.status_code, 200)