import random

def get_lots(request):
    """Responds to any HTTP request.
    Args:
        request (flask.Request): HTTP request object.
    Returns:
        The response text or any set of values that can be turned into a
        Response object using
        `make_response <http://flask.pocoo.org/docs/1.0/api/#flask.Flask.make_response>`.
    """
    #request_json = request.get_json()
    lots_id = random.randint(1,60)
    img_src = "http://www.chance.org.tw/%E7%B1%A4%E8%A9%A9%E9%9B%86/%E5%85%AD%E5%8D%81%E7\
    %94%B2%E5%AD%90%E7%B1%A4/台北新莊地藏庵六十甲子籤/籤詩網%20-%20台北新莊地藏庵六十甲子籤_第"+ "\
    :02d}".format(lots_id) + "籤.jpg"

    # Set CORS headers for the main request
    headers = {
        'Access-Control-Allow-Origin': '*'
    }

    return (img_src, 200, headers)