# ![Koffeemate Logo](https://raw.githubusercontent.com/CodemateLtd/Koffeemate/master/art/koffeemate_logo.png)

[![Build Status](https://travis-ci.org/CodemateLtd/Koffeemate.svg?branch=master)](https://travis-ci.org/CodemateLtd/Koffeemate)

![Koffeemate Screenshot](https://github.com/CodemateLtd/Koffeemate/blob/master/art/screenshot_coffee_incoming.png)

# What?
We at Codemate **love** coffee. Numerous cups of that sweet black nectar are brewed every day at our office. Coffee is what keeps us productive, creative and especially on Mondays, awake. Simply put, we just couldn't function without it.

# Okay, but still, what?
Koffeemate was made for three purposes:

1. Informing others on Slack when freshly brewed coffee is available
2. Gathering interesting data of our coffee consumption
3. Publicly shaming those who leave a giant mess behind while they try to brew coffee.

This project is also a great opportunity to practice some Android testing and architecture skills. 

# How does it work?
The system is very elegant: we have a cheap Android phone glued to the wall next to our coffee machine. Running this app is the only thing that phone can do. We made this extra secure by taping some cardboard over the physical buttons.

Every time someone starts the coffee machine, they also press the coffee pot button on the center of the screen. After exactly 7 minutes, which is the most appropriate delay we've found, everyone in the special Slack channel gets notified.

However, if someone fails the coffee brewing process, they can be publicly shamed by using the "Log an accident" button.

# That's neat! We want it too!
Of course you do. Here's the steps to get it working:

## Create a bot user on Slack
1. Go to [the custom integrations page on Slack](https://api.slack.com/custom-integrations), and click the ```Create a bot user``` button.
2. Click the green ```Add Configuration``` button on the left.
3. Choose a username for your bot and click ```Add bot integration```.
4. Configure your bot the way you like. **Take note of the API token, you'll need it next.**
5. **IMPORTANT:** Invite the newly-made bot to any channels you would like the coffee announcements to be made on.

## Make it work with Koffeemate
1. Change to a folder of your liking and do a ```git clone https://github.com/CodemateLtd/Koffeemate.git```
2. **Don't open the project yet.**
3. Create an empty ```koffeemate.properties``` file in your **app module** with the following contents:

**Koffeemate/app/koffeemate.properties:**
```groovy
SLACK_AUTH_TOKEN = your_api_token // Replace with the actual token
```

Now you can open the project in Android Studio. Make sure you have the Kotlin plugin installed.

Install the app to an old phone, glue it to a wall near a coffee machine and enjoy!

# Contributing

We'd love to have you contribute, and we do not have any strict rules.

However, here's some tips for a great start:

* We love PR's related to test coverage / code cleanliness improvements.
* Out of ideas? Look for the [issue tracker](https://github.com/CodemateLtd/Koffeemate/issues) for something to do. Tell if you want to do something, and we'll assign it to you.
* If you have something big in mind, create an issue first. Major functionality changes might not necessarily get merged.

# License

```
Copyright 2016 Codemate Ltd

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
