from django.db.models.signals import post_save, post_migrate
from django.contrib.auth.models import User
from django.dispatch import receiver
from .models import Player
from django.apps import apps

@receiver(post_save, sender=User)
def create_player(sender, instance, created, **kwargs):
    if created:
        Player.objects.create(user=instance, nickname=instance.username)

@receiver(post_migrate)
def ensure_default_vessels(sender, **kwargs):
    Vessel = apps.get_model('api', 'Vessel')

    expected_vessels = [
        {'name': 'Patrol Boat', 'size': 1, 'image': 'SeaWarfareSet/PatrolBoat/ShipPatrolHull.png'},
        {'name': 'Destroyer', 'size': 2, 'image': 'SeaWarfareSet/Destroyer/ShipDestroyerHull.png'},
        {'name': 'Cruiser', 'size': 3, 'image': 'SeaWarfareSet/Cruiser/ShipCruiserHull.png'},
        {'name': 'Submarine', 'size': 4, 'image': 'SeaWarfareSet/Submarine/ShipSubMarineHull.png'},
        {'name': 'Carrier', 'size': 5, 'image': 'SeaWarfareSet/Carrier/ShipCarrierHull.png'},
    ]

    db_vessels = Vessel.objects.all()

    # If there's an error we create all the vessels again
    if db_vessels.count() != len(expected_vessels) or any(
        not Vessel.objects.filter(name=v['name'], size=v['size'], image=v['image']).exists()
        for v in expected_vessels
    ):
        Vessel.objects.all().delete()
        for v in expected_vessels:
            Vessel.objects.create(**v)