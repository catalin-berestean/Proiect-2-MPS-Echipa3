<template>
  <v-data-table
      :headers="headers"
      :search="search"
      :items="resources"
  >
    <template v-slot:[`item.state`]="{ item }">
      <v-chip
          :color="getColor(item.state)"
          dark
      >
        {{ item.state }}
      </v-chip>
    </template>

    <template v-slot:top>
      <v-text-field
          v-model="search"
          append-icon="mdi-magnify"
          label="Search"
          class="mx-10"
      ></v-text-field>
      <v-dialog
          v-model="dialog"
          max-width="500px"
      >

        <v-card>
          <v-card-title>
            <span class="headline">Book this resource</span>
          </v-card-title>

          <v-card-text>
            <v-container>
              <v-form ref="form">
                <v-text-field
                    v-model="reason"
                    label="Reason of reserving the resource"
                ></v-text-field>
                <v-datetime-picker
                    label="Estimated time"
                    v-model="datetime"
                > </v-datetime-picker>
                <v-alert
                    dense
                    v-show="display_error"
                    type="error"
                    elevation="4"
                    dismissible
                >
                  {{ error_message }}
                </v-alert>
              </v-form>

            </v-container>
          </v-card-text>

          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn
                color="blue darken-1"
                text
                @click="close"
            >
              Cancel
            </v-btn>
            <v-btn
                color="blue darken-1"
                text
                @click="book"
            >
              Book
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>

      <v-dialog
          v-model="display_notify"
          max-width="500px"
      >

        <v-card>
          <v-card-title>
            <span class="headline">Get notifications about this resource</span>
          </v-card-title>

          <v-card-text>
            <v-container>
              <v-checkbox
                  v-model="notify"
                  label="Keep me notified"
              ></v-checkbox>
            </v-container>
          </v-card-text>

          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn
                color="blue darken-1"
                text
                @click="close"
            >
              Cancel
            </v-btn>
            <v-btn
                color="blue darken-1"
                text
                @click="close"
            >
              Ok
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>








      <v-dialog v-model="showHistDialog">
        <v-card>
          <v-card-title>
            <span class="headline">Resource Booking History</span>
          </v-card-title>

          <v-card-text>
            <v-data-table
                :headers="historyHeaders"
                :items="historyRows"
            >
            </v-data-table>
          </v-card-text>

          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn
                color="blue darken-1"
                text
                @click="closeHistory"
            >
              Cancel
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>


    </template>
    <template v-slot:[`item.actions`]="{ item }">
      <v-icon v-if= "role === 'EDIT' && item.state === 'AVAILABLE'"
              @click="bookItem(item)"
      >
        mdi-calendar-edit
      </v-icon>
      <v-icon v-else
              @click="bookItem(item)"
              disabled
      >
        mdi-calendar-edit
      </v-icon>

      <v-icon
          @click="showHistory(item)"
      >
        mdi-history
      </v-icon>
      <v-icon v-if= "item.state === 'BOOKED'"
              @click="notifyItem(item)"
      >
        mdi-message-text-outline
      </v-icon>
    </template>
    <template v-slot:no-data>
      <v-btn
          color="primary"
          @click="getResources()"
      >
        Reset
      </v-btn>
    </template>
  </v-data-table>
</template>

<script>
import axios from "axios";
import moment from 'moment';

const API_PATH = "http://localhost:8081/api";

export default {
  name: "ResourcesUser.vue",

  two_digits: (value) => {
    if (value < 0) {
      return '00';
    }
    if (value.toString().length <= 1) {
      return `0${value}`;
    }
    return value;
  },

  data: () => ({
    role: '',
    reason: '',
    datetime: '',
    notify: '',
    display_error: false,
    display_notify: false,
    error_message: '',

    search: '',
    dialog: false,
    dialogDelete: false,
    showHistDialog: false,

    expanded: [],
    singleExpand: true,

    drawer: false,
    group: null,
    countSpaces: 2,
    now: Math.trunc((new Date()).getTime() / 1000),

    editedIndex: -1,
    editedItem: {
      id: '',
      description: '',
      type: '',
      state: '',
    },
    defaultItem: {
      id: '',
      description: '',
      type: '',
      state: '',
    },

    intervalId: '',

    headers: [
      { text: 'Resource name', value: 'name', align: 'start', sortable: true},
      { text: 'Description', value: 'description', sortable: true },
      { text: 'Type', value: 'type', align: 'center', sortable: true},
      { text: 'State', value: 'state', align: 'center', sortable: true},
      { text: 'Time left', value: 'estimatedTimeBooking', align: 'center', sortable: false },
      { text: 'Actions', value: 'actions', align: 'center', sortable: false }
    ],

    resources: [],

    historyHeaders: [
      {text: 'username', value: 'user.username', sortable: true},
      {text: 'First name', value: 'user.firstName', sortable: true},
      {text: 'Last name', value: 'user.lastName', sortable: true},
      {text: 'Booking reason', value: 'bookingReason', sortable: false},
      {text: 'Booked at', value: 'fromBookingTime', align: 'center', sortable: true},
      {text: 'Booked until', value: 'toBookingTime', align: 'center', sortable: true}
    ],
    historyRows: []
  }),
  props: {
    dateTimer: {
      type: String
    }
  },
  computed: {
    formTitle () {
      return this.editedIndex === -1 ? 'New Item' : 'Book this resource'
    },
  },
  watch: {
    dialog (val) {
      val || this.close()
    },
    dialogDelete (val) {
      val || this.closeDelete()
    },
  },
  mounted: function() {
    this.getResources();
    this.role = localStorage.getItem("role");
    this.intervalId = window.setInterval(() => {
      this.getResources();
    },1000);
  },

  methods: {
    minutes(x) {
      return Math.trunc(x / 1000 / 60) % 60;
    },
    dateInMilliseconds() {
      return Math.trunc(Date.parse(this.date) / 1000)
    },
    seconds(x) {
      return Math.trunc((x / 1000)) % 60;
    },
    hours(x) {
      return Math.trunc(x / 1000 / 60 / 60) % 24;
    },
    days(x) {
      return Math.trunc(x / 1000 / 60 / 60 / 24);
    },
    convert(date) {
      let newDate = new Date(date) - new Date().getTime();
      return this.days(newDate) + 'd ' + this.hours(newDate) + 'h ' + this.minutes(newDate) + 'm ' + this.seconds(newDate) + 's ';
    },
    getResources() {
      axios.get(API_PATH + "/resources")
          .then(response => {
            this.resources = response.data;
            this.resources.map(r => {
              if(r.estimatedTimeBooking) {
                r.estimatedTimeBooking = this.convert(r.estimatedTimeBooking);
                return r;
              }
            })
          })
    },
    getColor(state) {
      if(state === 'AVAILABLE') {
        return 'green'
      }
      return 'red';
    },

    bookItem(item) {
      this.editedIndex = this.resources.indexOf(item)
      this.editedItem = Object.assign({}, item)
      this.dialog = true
    },

    notifyItem(item) {
      this.editedIndex = this.resources.indexOf(item)
      this.editedItem = Object.assign({}, item)
      this.display_notify = true
    },

    close () {
      this.dialog = false;
      this.display_error = false;
      this.display_notify = false;
      this.$nextTick(() => {
        this.editedItem = Object.assign({}, this.defaultItem)
        this.editedIndex = -1
      })
    },
    format_date(value) {
      if(value) {
        return moment(String(value)).format("MM/DD/YYYY hh:mm A");
      }
    },
    book () {
      axios
          .put(API_PATH + "/resources/book/" + this.editedItem.id, {
            estimatedTimeBooking: this.datetime,
            bookingReason: this.reason,
          })
          .then((res) => {
            console.log(res);
            this.error_message = false;
            this.resources = this.getResources();
            this.close();
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
    },

    getHistory(item) {
      axios.get(API_PATH + "/resources/" + item.id + "/history")
          .then(response => {
            this.historyRows = response.data;
            this.historyRows.map(row => {
              row.fromBookingTime = this.format_date(row.fromBookingTime);
              row.toBookingTime = this.format_date(row.toBookingTime);
              return row;
            })
          })
    },

    showHistory(item) {
      console.log(item);
      this.showHistDialog = true;
      this.getHistory(item)
    },

    closeHistory(){
      this.showHistDialog = false;
    },
  }
}
</script>

<style scoped>

</style>