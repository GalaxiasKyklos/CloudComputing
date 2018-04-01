# Geocode Map-Reduce

## Saúl Ponce

## is699399

### What works

- Everything works

### Videos

- EMR cluster: https://youtu.be/egkyJ8jBxAI

- EC2 instance: https://youtu.be/XKopluQNPPE

### Technical report
- `GeocodeDriver` sets up the job specifying the inputs and outputs

- `GeocodeMapper` formats the data, giving it a 'tag' of `geocode` if it is a geocode, or the subject if it is a image

- `GeocodeReducer` first looks for a geocode tag in its inputs, if it find one, then it relates it with the rest, finding matches in the keys and values and checking if the geocode is in the radious of a city in the list

- `visualizer` opens the result file in S3, and does the magic to show the map 
