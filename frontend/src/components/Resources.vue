<template>
    <v-card>
      <div align="center">
        <br v-for="c in countSpaces" :key="c">
        <form style="width: 50%;">
          <v-text-field
              v-model="resourceName"
              label="Resource name"
              required
              solo
              placeholder="Resource name"
          ></v-text-field>
          <v-text-field
              v-model="resourceDescription"
              label="Description"
              required
              solo
              placeholder="Resource description"
          ></v-text-field>
          <v-text-field
              v-model="resourceType"
              label="Resource type"
              required
              solo
              placeholder="Resource type"
          ></v-text-field>
          <br v-for="c in countSpaces" :key="c">
          <v-btn
              color="success"
              @click="addNewResource"
          >
            Add resource
          </v-btn>
        </form>
        <br v-for="c in countSpaces" :key="c">
      </div>
    </v-card>
</template>

<script>
export default {
  name: "Resources.vue",
  data: () => ({
    countSpaces: 3,
    resourceName: '',
    resourceDescription: '',
    resourceType: '',
  }),
  methods: {
    addNewResource() {
      //  method to add a new resource
    }
  }
}
</script>

<style scoped>

</style>