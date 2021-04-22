Feature: Create Blog with Year-Date Structure

  Background:
    Given I've logged in as an existing user

  Scenario: Create a new blog article in odd-e blog
    Given I add a new blog notebook "odd-e blog"
    And there is a blog site links to blog notebook "odd-e blog"
    When I add a new blog article in "odd-e blog" with title "Why it is so confusing?"
    Then I open the Blog page
#    And I should see a blog post titled 'Hello World' on the Blog page

  @ignore
  Scenario: After a blog post is created, the blog post's year should appear on blog-site's side navbar
    Given There are no existing blog created in 2021
    And I create a new blog post "How to use Donut" on 20 Apr 2021
    When I visit the blog-site
    Then I should see 2021 on the blog-site's side navbar

  @ignore
  Scenario:  After two blog posts with different years are created, two years should appear on blog-site's side navbar
    Given There is a blog post with title "Post 1" created in 31 Dec 2019
    And There is a blog post with title "Post 2" created in 01 Jan 2020
    When I visit the blog-site
    Then I should see 2019 and 2021 on the blog-site's side navbar

  @ignore
  Scenario: After creating a blog article, the year note is automatically created
    Given There is a Blog Notebook called odd-e blog
      | Title      | Description |
      | odd-e blog | test        |
    When after creating a blog article "First Article"
      | Title      | Description |
      | First Article | test        |
    Then I should see "odd-e blog, {YYYY}, {MMM}, First Article" in breadcrumb