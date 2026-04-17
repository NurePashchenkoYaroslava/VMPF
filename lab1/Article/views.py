from django.shortcuts import render
from .models import Article
from rest_framework.decorators import api_view
from rest_framework.response import Response
from .serializers import ArticleSerializer

def author_articles(request, author_id):
    articles = Article.objects.filter(author=author_id)
    context = {
        'articles' : articles
    }
    return render (request, 'article/list.html', context)

def article_detail(request, article_id):
    article = Article.objects.get(id=article_id) 
    context = {
        'article': article 
    }
    
    return render(request, 'article/detail.html', context)

@api_view(['GET'])
def api_articles(request):
    articles = Article.objects.all()
    serializer = ArticleSerializer(articles, many=True)

    return Response(serializer.data)