# Lab 1 - Cloud Storage Image Search

## Saúl Ponce

## is699399

### What works

- Everything works

### File logic

- `keyvaluestore.js`
  - The init method chekcs if the table exists and if it succeeds calls whendone.
  - The get method makes a query for the search parameter in the current table, returns the result once the promice has been fulfilled.

- `/Lab1/ITESO_CC_LP1MS2/app.js`
  - The processData function looks for the url from a given search word and sends the urls to the front end.

- `/Lab1/ITESO_CC_LP1MS2/public/js/app.js`
  - Has the logic to update the modal images and hide / show the previous and next buttons.
