#!/usr/bin/env python3
import requests
import bs4
import re

import pprint


class Session():
    def __init__(self, username, password):
        self.session = requests.Session()
        self.dashboard_soup = self.login(username, password)

    def login(self, username, password):
        payload = {"sendLogin": 1,
                   "idp_selection": "https://idp.scc.kit.edu/idp/shibboleth",
                   "target": "https://ilias.studium.kit.edu/shib_login.php",
                   "home_organization_selection": "Mit KIT-Account anmelden"}
        response = self.session.post(
            "https://ilias.studium.kit.edu/Shibboleth.sso/Login",
            data=payload)

        soup = bs4.BeautifulSoup(response.text, 'html.parser')
        form = soup.find('form', attrs={'class': 'form2', 'method': 'post'})
        action = form['action']

        # parse and login
        credentials = {"_eventId_proceed" : "", "j_username": username, "j_password": password}
        url = "https://idp.scc.kit.edu" + action

        response = self.session.post(url, data=credentials)

        html_doc = response.text

        soup = bs4.BeautifulSoup(html_doc, 'html.parser')
        relay_state = soup.find('input', attrs={'name': 'RelayState'})
        saml_response = soup.find('input', attrs={'name': 'SAMLResponse'})

        if not relay_state:
            raise Exception('wrong credentials!')

        payload = {'RelayState': relay_state['value'],
                   'SAMLResponse': saml_response['value'],
                   '_eventId_proceed': ''}
        dashboard_html = self.session.post(
            "https://ilias.studium.kit.edu/Shibboleth.sso/SAML2/POST",
            data=payload).text

        return bs4.BeautifulSoup(dashboard_html, 'html.parser')

    def get_courses(self):
        return self.extract_items(self.dashboard_soup)

    def get_content(self, link):
        html = self.session.get(link).text
        soup = bs4.BeautifulSoup(html, 'html.parser')
        return self.extract_items(soup)

    def add_subcontent(self, courses, deepness = -1):
        for course in courses:
            href = course["href"]
            if deepness != 0 and re.search("crs|fold", href):
                course["content"] = self.get_content(href)
                deepness -= 1
                self.add_subcontent(course["content"], deepness)

    def extract_items(self, soup):
        a_tags = soup.find_all('a', class_='il_ContainerItemTitle')
        return [self.format_tag(a_tag) for a_tag in a_tags]

    def format_tag(self, a_tag):
        base_url = "https://ilias.studium.kit.edu/"

        href = a_tag["href"]
        if not re.match(base_url, href):
            href = base_url + href

        return {"name": a_tag.contents[0], "href": href}


def parse(username, password):
    session = Session(username, password)
    courses = session.get_courses()
    session.add_subcontent(courses)
    return courses


if __name__ == "__main__":
    import sys
    import pprint
    username = sys.argv[1]
    password = sys.argv[2]

    session = Session(username, password)
    courses = session.get_courses()

    pprint.pprint(courses)
