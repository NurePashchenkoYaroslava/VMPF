from django.db import models

class Article(models. Model):
    title = models. CharField(max_length=255)
    text = models. TextField()
    published_at = models. DateTimeField (auto_now_add=True)
    author = models. ForeignKey('auth.User', on_delete=models.CASCADE)

