<template>
    <v-card>
      <div align="center">
        <br v-for="x in countSpaces" :key="x">
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
              label="Resource description"
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
          <br><br>
          <v-alert
              dense
              v-show="display_success"
              type="success"
              elevation="4"
          >
            {{ success_message }}
          </v-alert>
          <v-alert
              dense
              v-show="display_error"
              type="error"
              elevation="4"
          >
            {{ error_message }}
          </v-alert>
        </form>
        <br v-for="c in countSpaces" :key="c.id">
      </div>
    </v-card>
</template>

<script>
import axios from "axios";

const API_PATH = "http://localhost:8081/api";
export default {
  name: "ResourcesAdmin.vue",
  data: () => ({
    display_success: false,
    display_error: false,
    error_message: '',
    success_message: 'Resource was added successfully',
    countSpaces: 3,
    resourceName: '',
    resourceDescription: '',
    resourceType: ''
  }),
  methods: {
    addNewResource() {
      axios
          .post(API_PATH + "/resources", {
            name: this.resourceName,
            description: this.resourceDescription,
            type: this.resourceType
          })
          .then((res) => {
            console.log(res);
            this.display_success = true;
          })
          .catch((error) => {
            let msg = error.response.data["message"]
            if(!msg) {
              this.error_message = "Error processing the request";
            } else {
              this.error_message = msg;
            }
            this.display_error = true;
          });
    }
  }
}
</script>

<style scoped>

</style>