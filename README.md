# EasyItems

EasyItems is an open-source, data-driven custom item framework for Paper.

## Target

- Minecraft: 26.2
- Paper: 26.2
- Java: 25
- Namespace: `eis`

Paper's 26.2 API uses the newer data-component API, including `ITEM_MODEL`, which EasyItems uses for its item-model layer.

## Current MVP

- YAML item definitions
- Logical IDs such as `eis:ruby`
- Persistent item identification
- Item Model data component
- `/give @s eis:ruby`
- `/give @s eis:ruby 16`
- `/eis give <player> eis:<id> [amount]`
- `/eis list`
- `/eis info <id>`
- `/eis reload`

The first `/give` implementation intentionally handles only `@s` for EasyItems IDs. Normal vanilla `/give` commands are left alone.

## Build locally

Use JDK 25 and Gradle 9.1+:

```bash
gradle clean build
```

The JAR is created at:

```
build/libs/EasyItems-0.1.0.jar
```

## GitHub Actions

A manual workflow is included at:

```
.github/workflows/build.yml
```

Run it from:

```
GitHub -> Actions -> Build EasyItems -> Run workflow
```

The workflow:

1. Checks out the repository.
2. Installs Temurin Java 25.
3. Sets up Gradle 9.1.0.
4. Runs `gradle --no-daemon clean build`.
5. Uploads the generated JAR as the `easyitems` artifact.

## Configuration

Example:

```yaml
items:
  ruby:
    material: DIAMOND
    display-name: "&cルビー"
    model: ruby
    lore:
      - "&7EasyItems sample item"
      - "&8ID: eis:ruby"
```

The configured `material` is the vanilla backing item. EasyItems exposes the logical item as `eis:<id>` and stores the logical ID in persistent item data.

## Roadmap

- Better `/give` argument and selector handling
- TAB completion for `/give @s eis:<id>`
- Automatic Resource Pack generation
- Custom textures/models
- Recipes
- More item components
- Java API for other plugins
- Versioned compatibility modules
